package com.ambrosia.content_service.follow.model.dto;

import java.time.Instant;

import com.ambrosia.content_service.post.model.dto.response.UserResponse;

public record UserFollowResponse(
    UserResponse followedUser,
    Instant followedAt
) {}
