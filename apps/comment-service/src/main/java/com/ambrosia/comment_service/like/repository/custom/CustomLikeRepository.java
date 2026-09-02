package com.ambrosia.comment_service.like.repository.custom;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.ambrosia.comment_service.like.model.dto.LikeDelta;
import com.ambrosia.comment_service.like.model.entity.CommentLike;

public interface CustomLikeRepository {
    List<Long> getUserLikesForPostComments(long postId, UUID userId);
    List<Long> getUserLikesForCommentTree(long commentId, UUID userId);
    List<LikeDelta> batchDeleteAll(Collection<CommentLike> commentLike);
    List<LikeDelta> batchSaveAll(Collection<CommentLike> commentLike);
}
