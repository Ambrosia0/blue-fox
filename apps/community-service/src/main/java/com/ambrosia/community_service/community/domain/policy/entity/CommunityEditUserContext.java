package com.ambrosia.community_service.community.domain.policy.entity;

import java.util.UUID;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommunityEditUserContext(
    UUID ownerId,
    boolean isAnyTargetBanned,
    boolean isAllTargetsExists
) implements PolicyData{}
