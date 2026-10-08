package com.ambrosia.content_service.post.domain.policy.entity;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommunityData(
    boolean isModerator,
    boolean isFollowed,
    boolean isBanned,
    boolean isPrivate
) implements PolicyData {}
