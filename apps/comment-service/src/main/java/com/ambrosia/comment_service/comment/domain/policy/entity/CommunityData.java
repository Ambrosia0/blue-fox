package com.ambrosia.comment_service.comment.domain.policy.entity;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommunityData(
    boolean isBanned,
    boolean isModerator,
    boolean isPrivate,
    boolean isFollowed
) implements PolicyData{}
