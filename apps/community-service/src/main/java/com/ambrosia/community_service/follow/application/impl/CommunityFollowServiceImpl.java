
package com.ambrosia.community_service.follow.application.impl;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.core.domain.policy.entity.CommunityFollowUserContext;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowCreateResponse;
import com.ambrosia.community_service.follow.api.mapper.CommunityFollowMapper;
import com.ambrosia.community_service.follow.application.CommunityFollowService;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;
import com.ambrosia.community_service.follow.domain.policy.CommunityFollowPolicy;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRepository;
import com.ambrosia.community_service.infrastructure.kafka.utils.CommunityFollowEventFactory;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CommunityFollowServiceImpl implements CommunityFollowService{

    private final CommunityFollowRepository communityFollowRepository;

    private final GenericPolicyContext genericPolicyContext;

    private final ApplicationEventPublisher applicationEventPublisher;

    private final CommunityFollowMapper communityFollowMapper;

    @Override
    public CommunityFollowCreateResponse followCommunity(long communityId, Actor actor) {
        var context = (CommunityFollowUserContext)genericPolicyContext
            .evaluate(actor, CommunityFollowPolicy.class, communityId);

        var follow = CommunityFollow.buidler()
            .userId(actor.id())
            .communityId(communityId)
            .requiresApproval(context.isPrivate() && !context.isModerator())
            .build();

        communityFollowRepository.persist(follow);
        if(!context.isPrivate())
            applicationEventPublisher.publishEvent(
                CommunityFollowEventFactory.createFollow(actor.id(), communityId)
            );
        return communityFollowMapper.toResponse(follow);
    }
    
    @Override
    public void removeFollow(long communityId, Actor actor) {
        if(communityFollowRepository.remove(actor.id(), communityId))
            applicationEventPublisher.publishEvent(
                CommunityFollowEventFactory.createUnfollow(actor.id(), communityId)
            );
    }
}
