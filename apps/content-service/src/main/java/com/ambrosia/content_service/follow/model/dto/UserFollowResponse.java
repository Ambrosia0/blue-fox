package com.ambrosia.content_service.follow.model.dto;

import java.time.Instant;
import java.util.UUID;

public record UserFollowResponse(
    UUID followedUserId,
    Instant followedAt
) {}
