package com.ambrosia.content_service.post.domain.policy.entity;

import java.util.Optional;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record PostCreatePolicyData(
    Optional<CreateCommunityData> postedCommunity,
    Optional<CreatePostData> replyingPost
) implements PolicyData{}
