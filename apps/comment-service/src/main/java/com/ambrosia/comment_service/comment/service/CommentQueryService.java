package com.ambrosia.comment_service.comment.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.CommentData;
import com.ambrosia.comment_service.comment.model.dto.response.ScoredCommentData;

import jakarta.annotation.Nullable;

public interface CommentQueryService {
    List<ScoredCommentData> getCommentsForPost(
        long postId, 
        EventFilter eventFilter, 
        @Nullable UUID requestingUser
    );
    List<ScoredCommentData> getCommentTree(
        long commentId, 
        @Nullable UUID requestingUser
    );
    CommentData getComment(
        long commentId, 
        @Nullable UUID requestingUser
    );
}
