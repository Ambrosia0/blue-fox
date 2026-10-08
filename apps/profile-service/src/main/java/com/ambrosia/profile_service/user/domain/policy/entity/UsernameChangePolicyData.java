package com.ambrosia.profile_service.user.domain.policy.entity;

import java.time.Instant;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record UsernameChangePolicyData(
    Instant lastChangeInstant,
    boolean isUsernameClaimed
) implements PolicyData{}
