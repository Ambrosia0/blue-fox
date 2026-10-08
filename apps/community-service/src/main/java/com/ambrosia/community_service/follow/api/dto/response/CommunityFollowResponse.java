package com.ambrosia.community_service.follow.api.dto.response;

import java.time.Instant;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.user.model.entity.UserResponse;

public record CommunityFollowResponse(
    UserResponse userResponse,
    Instant createdAt,
    FollowFilter.Type type
) {}
