package com.ambrosia.content_service.post.domain.policy.entity;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record PostPublishPolicyData(
    UUID authorId,
    Optional<CreateCommunityData> postedCommunity,
    Optional<CreateCommunityData> replyingPostCommunity
) implements PolicyData{}
