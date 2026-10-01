package com.ambrosia.comment_service.comment.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;

import jakarta.annotation.Nullable;

public interface CommentQueryRepository {
    List<ScoredCommentData> getRootCommentsForPost(long postId, @Nullable UUID userId, EventFilter eventFilter, int pageSize);
    List<ScoredCommentData> getTreeForPostComment(long commentId, @Nullable UUID userId);
    Optional<CommentData> getComment(long commentId, @Nullable  UUID userId);
}
