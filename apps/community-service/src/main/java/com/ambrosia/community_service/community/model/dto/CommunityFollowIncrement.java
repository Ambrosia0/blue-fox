package com.ambrosia.community_service.community.model.dto;

public record CommunityFollowIncrement(
    long communityId,
    int delta
) {}
