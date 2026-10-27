package com.ambrosia.comment_service.comment.domain.repository;

import java.util.Optional;
import java.util.UUID;


import com.ambrosia.comment_service.comment.api.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.domain.entity.Comment;
import com.ambrosia.comment_service.comment.domain.entity.DeletedComment;

public interface CommentRepository{
    int countByPostId(long postId);
    boolean existsByPostId(long postId);

    Optional<Long> getPostId(long commentId);
    
    Optional<DeletedComment> hideCommentById(long commentId);

    boolean existsByIdAndIsVisibleIsTrue(long id);

    Optional<CreateCommentResponse> insert(Comment comment);

    Optional<CreateCommentResponse> findCreateProjection(long commentId, UUID userId);
    Optional<Comment> findById(Long id);
}
