package com.ambrosia.community_service.core.domain.policy.entity;

import java.util.Set;

import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommunityModeratorUserContext(
    Set<ScopeEnum> scopes,
    boolean isTargetExist,
    boolean isTargetModerator,
    boolean isTargetBanned
) implements PolicyData{}
