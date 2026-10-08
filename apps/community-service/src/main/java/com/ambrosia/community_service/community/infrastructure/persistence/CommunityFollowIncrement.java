package com.ambrosia.community_service.community.infrastructure.persistence;

public record CommunityFollowIncrement(
    long communityId,
    int delta
) {}
