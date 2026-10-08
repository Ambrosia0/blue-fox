package com.ambrosia.community_service.community.application.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.community.api.dto.request.CommunityCreate;
import com.ambrosia.community_service.community.api.dto.request.CommunityEdit;
import com.ambrosia.community_service.community.api.dto.request.FileMetadata;
import com.ambrosia.community_service.community.api.dto.response.AvatarUploadResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityCreateResponse;
import com.ambrosia.community_service.community.api.dto.response.CommunityEditResponse;
import com.ambrosia.community_service.community.api.mapper.CommunityMapper;
import com.ambrosia.community_service.community.application.AvatarManager;
import com.ambrosia.community_service.community.application.CommunityIndexService;
import com.ambrosia.community_service.community.application.CommunityManageService;
import com.ambrosia.community_service.community.application.policy.CommunityEditArg;
import com.ambrosia.community_service.community.domain.entity.Community;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityCreatePolicy;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityDeletePolicy;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityEditPolicy;
import com.ambrosia.community_service.community.domain.policy.manage.CommunityOwnerEditPolicy;
import com.ambrosia.community_service.community.domain.repository.CommunityRepository;
import com.ambrosia.community_service.community.infrastructure.cache.CommunitySlugCache;
import com.ambrosia.community_service.community.utils.AvatarIdGenerator;
import com.ambrosia.community_service.community.utils.CommunityEventFactory;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;
import com.ambrosia.outbox.kafka.KafkaOutboxService;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityManageServiceImpl implements CommunityManageService{
    private final ApplicationEventPublisher eventPublisher;

    private final AvatarManager avatarService;

    private final CommunityRepository communityRepository;

    private final KafkaOutboxService kafkaOutboxService;

    private final CommunityIndexService communityIndexService;

    private final CommunitySlugCache communitySlugCache;

    private final CommunityMapper communityMapper;

    private final AvatarIdGenerator avatarIdGenerator;

    private final GenericPolicyContext genericPolicyContext;

    @Transactional
    @Override
    public CommunityCreateResponse createCommunity(CommunityCreate communityCreate, Actor actor) {
        genericPolicyContext.evaluate(actor, CommunityCreatePolicy.class, null);

        var community = communityRepository.save(Community.builder()
            .displayedName(communityCreate.displayedName())
            .slug(communityCreate.slug())
            .ownerId(actor.id())
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
    public CommunityEditResponse editCommunityInfo(long communityId, CommunityEdit communityEdit, Actor actor) {
        genericPolicyContext.evaluate(
                actor, 
                CommunityEditPolicy.class, 
                CommunityEditArg.create(communityId, communityEdit.scopes().keySet())
            );
        
        if(communityEdit.ownerId() != null){
            genericPolicyContext.evaluate(actor, CommunityOwnerEditPolicy.class, null);
        }

        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());

        community.update(
            communityEdit.displayedName(),
            communityEdit.description(),
            communityEdit.tags(),
            communityEdit.rules(),
            communityEdit.ownerId(),
            communityEdit.scopes()
        );

        community = communityRepository.save(community);

        var event = CommunityEventFactory.updateOperation(community);
        
        kafkaOutboxService.put(event);
        communityIndexService.reIndex(community);

        communitySlugCache.evictCommunity(community.getSlug());

        eventPublisher.publishEvent(event);

        return communityMapper.toEditResponse(community);
    }

    @Override
    public AvatarUploadResponse uploadAvatar(long communityId, @Nullable FileMetadata fileMetadata, Actor actor) {
        genericPolicyContext.evaluate(
            actor, 
            CommunityEditPolicy.class, 
            CommunityEditArg.create(communityId, null)
        );

        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());
            
        if(fileMetadata == null){
            avatarService.delete(communityId, community.getAvatarId());

            community.setAvatarId(null);

            community = communityRepository.save(community);
            communityIndexService.reIndex(community);
            return null;
        }
        var avatarId = avatarIdGenerator.generateAvatarId();
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
    public void validateAvatarUpload(long communityId, String avatarId) {
        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());
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
    public void deleteCommunity(long communityId, Actor actor) {
        genericPolicyContext.evaluate(
            actor, 
            CommunityDeletePolicy.class,
            CommunityEditArg.create(communityId, null)
        );
        var community = communityRepository.findById(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());

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
