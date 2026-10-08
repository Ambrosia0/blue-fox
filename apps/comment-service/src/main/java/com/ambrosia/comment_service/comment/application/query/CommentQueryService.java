package com.ambrosia.comment_service.comment.application.query;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;

import com.ambrosia.comment_service.comment.api.dto.CommentFilter;
import com.ambrosia.comment_service.comment.api.dto.EventFilter;
import com.ambrosia.comment_service.comment.api.dto.response.CommentData;
import com.ambrosia.comment_service.comment.api.dto.response.ScoredCommentData;
import com.ambrosia.library_policy.policy.Actor;

public interface CommentQueryService {
    List<ScoredCommentData> getCommentsForPost(
        long postId, 
        EventFilter eventFilter, 
        Actor actor
    );
    List<ScoredCommentData> getCommentTree(
        long commentId, 
        Actor actor
    );
    CommentData getComment(
        long commentId, 
        Actor actor
    );

    Slice<CommentData> getComments(
        CommentFilter commentFilter,
        UUID userId,
        Actor actor,
        int pageSize
    );
}
