package com.ambrosia.comment_service.comment.domain.policy.entity;

import java.util.UUID;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record DeletePolicyData(
    UUID authorId,
    boolean isModerator
) implements PolicyData{}
