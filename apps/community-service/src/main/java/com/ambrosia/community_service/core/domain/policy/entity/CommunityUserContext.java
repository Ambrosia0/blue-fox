package com.ambrosia.community_service.core.domain.policy.entity;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommunityUserContext(
    boolean isFollowed,
    boolean isBanned,
    boolean isModerator,
    boolean isPrivate
) implements PolicyData{}
