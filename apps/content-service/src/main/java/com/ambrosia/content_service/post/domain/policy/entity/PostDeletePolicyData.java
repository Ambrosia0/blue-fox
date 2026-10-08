package com.ambrosia.content_service.post.domain.policy.entity;

import java.util.UUID;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record PostDeletePolicyData(
    boolean hasDeletePermission,
    UUID authorId
) implements PolicyData{}
