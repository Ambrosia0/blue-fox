package com.ambrosia.comment_service.comment.domain.policy.entity;

import java.util.Optional;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommentPolicyData(
    Optional<CommunityData> community
) implements PolicyData {}
