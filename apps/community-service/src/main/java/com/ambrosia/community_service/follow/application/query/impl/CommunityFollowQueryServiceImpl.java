package com.ambrosia.community_service.follow.application.query.impl;

import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.core.application.policy.ModeratorPolicyArg;
import com.ambrosia.community_service.core.domain.policy.moderation.CommunityFollowManagePolicy;
import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowResponse;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowUserResponse;
import com.ambrosia.community_service.follow.application.query.CommunityFollowQueryRepository;
import com.ambrosia.community_service.follow.application.query.CommunityFollowQueryService;
import com.ambrosia.community_service.follow.application.query.CommunityFollowUserQueryRepository;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.GenericPolicyContext;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class CommunityFollowQueryServiceImpl implements CommunityFollowQueryService{
    private final CommunityFollowQueryRepository communityFollowQueryRepository;

    private final CommunityFollowUserQueryRepository communityUserFollowQueryRepository;

    private final GenericPolicyContext genericPolicyContext;

    @Override
    public Slice<CommunityFollowUserResponse> getUserFollows(Actor actor, FollowFilter filter, int pageSize) {
        return communityUserFollowQueryRepository.getUserFollows(actor.id(), filter, pageSize);
    }

    @Override
    public Slice<CommunityFollowResponse> getCommunityFollows(Actor actor, Long communityId, FollowFilter followFilter, int pageSize) {
        genericPolicyContext.evaluate(
                actor, 
                CommunityFollowManagePolicy.class,
                ModeratorPolicyArg.create(communityId, null)
            );

        return communityFollowQueryRepository.getFollows(communityId, followFilter, pageSize);
    }
}
