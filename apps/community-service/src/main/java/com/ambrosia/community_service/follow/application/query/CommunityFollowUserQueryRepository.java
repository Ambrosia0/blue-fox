package com.ambrosia.community_service.follow.application.query;

import java.util.UUID;

import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowUserResponse;

public interface CommunityFollowUserQueryRepository {
    Slice<CommunityFollowUserResponse> getUserFollows(UUID userId, FollowFilter filter, int pageSize);
}
