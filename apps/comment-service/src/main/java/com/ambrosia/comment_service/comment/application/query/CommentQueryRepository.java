package com.ambrosia.comment_service.comment.application.query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Slice;

import com.ambrosia.comment_service.comment.api.dto.CommentFilter;
import com.ambrosia.comment_service.comment.api.dto.EventFilter;
import com.ambrosia.comment_service.comment.api.dto.response.CommentData;
import com.ambrosia.comment_service.comment.api.dto.response.ScoredCommentData;

import jakarta.annotation.Nullable;

public interface CommentQueryRepository {
    List<ScoredCommentData> getRootCommentsForPost(
        long postId, 
        @Nullable UUID userId, 
        EventFilter eventFilter, 
        int pageSize
    );
    
    List<ScoredCommentData> getTreeForPostComment(
        long commentId, 
        @Nullable UUID userId
    );

    Optional<CommentData> getComment(
        long commentId, 
        @Nullable UUID userId
    );
    
    Slice<CommentData> getComments(
        CommentFilter commentFilter, 
        UUID userId, 
        @Nullable UUID requestingUser, 
        int pageSize
    );
}
