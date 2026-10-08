package com.ambrosia.community_service.follow.api.dto.response;

import java.time.Instant;

import com.ambrosia.community_service.community.application.query.model.CommunityPreview;
import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;

public record CommunityFollowUserResponse(
    CommunityPreview communityPreview,
    Instant followedAt,
    FollowFilter.Type type
) {
    public enum Type{
        FOLLOWED,
        REQUESTED;
    }
}
