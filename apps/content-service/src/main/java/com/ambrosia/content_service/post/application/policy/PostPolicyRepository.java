package com.ambrosia.content_service.post.application.policy;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.content_service.post.domain.policy.entity.CommunityData;
import com.ambrosia.content_service.post.domain.policy.entity.PostCreatePolicyData;
import com.ambrosia.content_service.post.domain.policy.entity.PostDeletePolicyData;
import com.ambrosia.content_service.post.domain.policy.entity.PostPublishPolicyData;
import com.ambrosia.content_service.post.domain.policy.entity.PostViewPolicyData;

import jakarta.annotation.Nullable;

public interface PostPolicyRepository {
    Optional<CommunityData> loadCommunityData(@Nullable UUID userId, Long communityId);
    PostCreatePolicyData loadForCreate(UUID userId, Long replyingPostId, @Nullable Long postedCommunityId);
    Optional<PostPublishPolicyData> loadForPublish(UUID userId, Long postId);
    Optional<PostDeletePolicyData> loadForDelete(UUID userId, Long postId);
    Optional<PostViewPolicyData> loadForView(UUID userId, Long postId);
}
