package com.ambrosia.community_service.community.service.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.community.model.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.model.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.model.dto.request.FileMetadata;
import com.ambrosia.community_service.community.model.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityCreateResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityEditResponse;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.AvatarService;
import com.ambrosia.community_service.community.service.CommunityBanService;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.service.cache.CommunitySlugCache;
import com.ambrosia.community_service.community.service.mappers.CommunityMapper;
import com.ambrosia.community_service.community.utils.AvatarIdGenerator;
import com.ambrosia.community_service.community.utils.CommunityEventFactory;
import com.ambrosia.community_service.community.utils.policy.CommunityAccessPolicy;
import com.ambrosia.community_service.core.AppConfiguration;
import com.ambrosia.community_service.core.CommunityIndexService;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.exception.community.ExceededOwnedCommunityLimitException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsBannedException;
import com.ambrosia.community_service.exception.community.UserIsOwnerException;
import com.ambrosia.community_service.user.service.UserService;
import com.ambrosia.outbox.kafka.KafkaOutboxService;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityManageServiceImpl implements CommunityManageService{
    private final ScopeLinkService scopeLinkService;
    
    private final ApplicationEventPublisher eventPublisher;

    private final AvatarService avatarService;

    private final CommunityRepository communityRepository;

    private final AppConfiguration appConfiguration;

    private final CommunityBanService communityBanService;

    private final KafkaOutboxService kafkaOutboxService;

    private final CommunityIndexService communityIndexService;

    private final CommunitySlugCache communitySlugCache;

    private final UserService userService;

    private final CommunityMapper communityMapper;

    @Transactional
    @Override
    public CommunityCreateResponse createCommunity(CommunityCreate communityCreate, UUID userId) {
        if(communityRepository.countOwned(userId) >= appConfiguration.getMaxOwnedCommunitiesPerUser())
            throw new ExceededOwnedCommunityLimitException();

        var community = communityRepository.save(Community.builder()
            .displayedName(communityCreate.displayedName())
            .slug(communityCreate.slug())
            .ownerId(userId)
            .tags(communityCreate.tags())
            .isPrivate(communityCreate.isPrivate())
            .build());
        
        var event = CommunityEventFactory.createOpration(community);
        kafkaOutboxService.put(event);
        communityIndexService.index(community);
        
        eventPublisher.publishEvent(event);

        return communityMapper.toCreateResponse(community);
    }

    @Transactional
    @Override
    public CommunityEditResponse editCommunityInfo(long communityId, CommunityEdit communityEdit, CommunityAccessPolicy policy) {
        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateOwnership(community);

        if(communityEdit.ownerId() != null){
            policy.validateOwnerEditing();
            community.setOwnerId(communityEdit.ownerId());
        }

        if(communityEdit.scopes() != null && !communityEdit.scopes().isEmpty()){
            if(community.getOwnerId() != null && communityEdit.scopes().containsKey(community.getOwnerId())){
                throw new UserIsOwnerException();
            }

            var userIds = communityEdit.scopes().keySet();
            if(communityBanService.isAnyBanned(userIds))
                throw new UserIsBannedException();
            
            if(!userService.isUsersExist(userIds)){
                throw new UserDoesntExistException();
            }
        }

        community = communityRepository.save(
            communityMapper.apply(community, communityEdit)
        );

        var event = CommunityEventFactory.updateOperation(community);
        
        kafkaOutboxService.put(event);
        communityIndexService.reIndex(community);

        communitySlugCache.evictCommunity(community.getSlug());

        eventPublisher.publishEvent(event);

        return communityMapper.toEditResponse(community);
    }

    @Override
    public AvatarUploadResponse uploadAvatar(long communityId, @Nullable FileMetadata fileMetadata, CommunityAccessPolicy policy) {
        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateOwnership(community);
        if(fileMetadata == null){
            avatarService.delete(communityId, community.getAvatarId());

            community.setAvatarId(null);

            community = communityRepository.save(community);
            communityIndexService.reIndex(community);
            return null;
        }
        var avatarId = AvatarIdGenerator.generateAvatarId();
        return AvatarUploadResponse.from(
            avatarService.upload(
                communityId,
                avatarId,
                fileMetadata
            ),
            avatarId
        );
    }

    @Override
    public void validateAvatarUpload(long communityId, String avatarId, CommunityAccessPolicy policy) {
        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateOwnership(community);
        avatarService.confirmUpload(communityId, avatarId);
        try {
            community.setAvatarId(avatarId);
            community = communityRepository.save(community);
            communityIndexService.reIndex(community);
        } catch (RuntimeException e) {
            try {
                avatarService.delete(communityId, avatarId);
            } catch (Exception ex) {
                e.addSuppressed(ex);
            }
            throw e;
        }
    }
    
    @Transactional
    @Override
    public void deleteCommunity(long communityId, CommunityAccessPolicy policy) {
        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());
        policy.validateOwnership(community);
        if(community.getAvatarId() != null)
            avatarService.delete(communityId, community.getAvatarId());
        communityRepository.deleteById(communityId);
        communityIndexService.removeFromIndex(community.getId(), community.getVersion());
    }

    @Override
    public boolean isSlugClaimed(String slug) {
        return communityRepository.existsBySlug(slug);
    }
}
