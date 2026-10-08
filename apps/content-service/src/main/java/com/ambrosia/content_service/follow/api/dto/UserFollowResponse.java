package com.ambrosia.content_service.follow.api.dto;

import java.time.Instant;

import com.ambrosia.content_service.post.api.dto.response.UserResponse;

public record UserFollowResponse(
    UserResponse followedUser,
    Instant followedAt
) {}
