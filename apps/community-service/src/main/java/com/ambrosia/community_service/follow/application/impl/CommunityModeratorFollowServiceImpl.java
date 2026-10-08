package com.ambrosia.community_service.follow.application.impl;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.core.application.policy.ModeratorPolicyArg;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityFollowManagePolicy;
import com.ambrosia.community_service.exception.follow.FollowRequestDoesntExist;
import com.ambrosia.community_service.follow.application.CommunityModeratorFollowService;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.infrastructure.kafka.utils.CommunityFollowEventFactory;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service
public class CommunityModeratorFollowServiceImpl implements CommunityModeratorFollowService{
    private final CommunityFollowRequestRepository communityFollowRequestRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final GenericPolicyContext genericPolicyContext;

    @Transactional
    @Override
    public void approveRequest(Long communityId, UUID approvedUser, Actor actor) {
        genericPolicyContext.evaluate(
            actor, 
            CommunityFollowManagePolicy.class, 
            ModeratorPolicyArg.create(communityId, null)
        );

        if(!communityFollowRequestRepository.approve(approvedUser, communityId))
            throw new FollowRequestDoesntExist();

        applicationEventPublisher.publishEvent(
            CommunityFollowEventFactory.createFollow(approvedUser, communityId)
        );
    }

    @Override
    public void declineRequest(Long communityId, UUID declinedUser, Actor actor) {
        genericPolicyContext.evaluate(
            actor, 
            CommunityFollowManagePolicy.class, 
            ModeratorPolicyArg.create(communityId, null)
        );

        if(!communityFollowRequestRepository.decline(declinedUser, communityId))
            throw new FollowRequestDoesntExist();
    }
}
