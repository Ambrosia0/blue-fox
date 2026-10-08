package com.ambrosia.community_service.follow.application.query;

import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowResponse;

public interface CommunityFollowQueryRepository {
    Slice<CommunityFollowResponse> getFollows(Long communityId, FollowFilter filter, int pageSize);
}
