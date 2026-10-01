package com.ambrosia.community_service.follow.model.dto.response;

import java.time.Instant;

import com.ambrosia.community_service.community.model.dto.response.CommunityPreview;

public record CommunityFollowResponse(
    CommunityPreview communityPreview,
    Instant followedAt
) {}
