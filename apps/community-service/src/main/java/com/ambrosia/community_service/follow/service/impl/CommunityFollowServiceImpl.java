
package com.ambrosia.community_service.follow.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.service.CommunityPrivacyService;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.exception.follow.DoesntFollowedException;
import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowRequestService;
import com.ambrosia.community_service.follow.service.CommunityFollowService;
import com.ambrosia.community_service.kafka.utils.CommunityFollowEventFactory;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommunityFollowServiceImpl implements CommunityFollowService{
    private final CommunityFollowRepository communityFollowRepository;

    private final CommunityFollowRequestService communityFollowRequestService;

    private final CommunityPrivacyService communityPrivacyService;

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void followCommunity(long communityId, UUID requestingUser) {
        if(communityFollowRepository.existsById(CommunityFollowKey.create(requestingUser, communityId)))
            throw new AlreadyFollowedException();
        
        var isPrivate = communityPrivacyService.isPrivate(communityId)
            .orElseThrow(() -> new CommunityDoesntExistException());

        if(isPrivate){
            communityFollowRequestService.createFollowRequest(requestingUser, communityId);
            return;
        }
        
        communityFollowRepository.optionalSave(CommunityFollow.create(requestingUser, communityId))
            .orElseThrow(() -> new AlreadyFollowedException());
        applicationEventPublisher.publishEvent(
            CommunityFollowEventFactory.createFollow(requestingUser, communityId));
    }

    @Override
    public Slice<CommunityFollow> getFollows(UUID requestingUser, int page) {
        return communityFollowRepository.findByUserId(requestingUser, Pageable.ofSize(10).withPage(page));
    }
    
    @Override
    public void removeFollow(long communityId, UUID requestingUser) {
        var res = communityFollowRepository.returningDelete(CommunityFollowKey.create(requestingUser, communityId));
        if(res == 0)
            throw new DoesntFollowedException();
        applicationEventPublisher.publishEvent(
            CommunityFollowEventFactory.createUnfollow(requestingUser, communityId));
    }

    @Override
    public List<UUID> getFollowedUsers(long communityId, int page) {
        return communityFollowRepository.findByCommunityId(communityId, Pageable.ofSize(10).withPage(page));
    }

}
