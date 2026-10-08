package com.ambrosia.community_service.follow.api.dto.response;

public record CommunityFollowCreateResponse(
    Type type
) {
    public enum Type{
        FOLLOWED,
        REQUESTED;
    }
}
