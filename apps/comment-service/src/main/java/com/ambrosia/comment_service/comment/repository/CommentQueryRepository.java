package com.ambrosia.comment_service.comment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.RootCommentData;
import com.ambrosia.comment_service.comment.model.dto.response.TreeCommentData;

public interface CommentQueryRepository {
    // List<Comment> getCommentsForPost(long postId);
    List<RootCommentData> getRootCommentsForPost(long postId, EventFilter eventFilter, int pageSize);
    List<TreeCommentData> getTreeForPostComment(long commentId);
    List<RootCommentData> getRootCommentsForPostWithLike(long postId, UUID userId, EventFilter eventFilter, int pageSize);
    List<TreeCommentData> getTreeForPostCommentWithLike(long commentId, UUID userId);
    Optional<TreeCommentData> getComment(long commentId);
    Optional<TreeCommentData> getCommentWithLike(long commentId, UUID userId);
}
