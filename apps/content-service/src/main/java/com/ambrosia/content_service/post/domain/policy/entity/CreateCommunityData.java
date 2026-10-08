package com.ambrosia.content_service.post.domain.policy.entity;

public record CreateCommunityData(
    Long communityId,
    boolean isModerator,
    boolean isFollowed,
    boolean isBanned,
    boolean isPrivate
) {}
