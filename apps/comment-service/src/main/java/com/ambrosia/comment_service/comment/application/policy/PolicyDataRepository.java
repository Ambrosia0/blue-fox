package com.ambrosia.comment_service.comment.application.policy;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.comment_service.comment.domain.policy.entity.CommentPolicyData;
import com.ambrosia.comment_service.comment.domain.policy.entity.DeletePolicyData;

public interface PolicyDataRepository {
    Optional<DeletePolicyData> loadDeleteData(UUID userId, Long commentId);
    Optional<CommentPolicyData> loadByPost(UUID userId, Long postId);
    Optional<CommentPolicyData> loadByComment(UUID userId, Long commentId);
}
