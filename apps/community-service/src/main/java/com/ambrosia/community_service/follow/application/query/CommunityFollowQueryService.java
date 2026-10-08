package com.ambrosia.community_service.follow.application.query;

import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowResponse;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowUserResponse;
import com.ambrosia.library_policy.policy.Actor;

public interface CommunityFollowQueryService {
    Slice<CommunityFollowUserResponse> getUserFollows(Actor actor, FollowFilter filter, int pageSize);
    Slice<CommunityFollowResponse> getCommunityFollows(Actor actor, Long communityId, FollowFilter followFilter, int pageSize);
}