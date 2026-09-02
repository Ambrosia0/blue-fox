package com.ambrosia.comment_service.comment.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.response.RootCommentData;
import com.ambrosia.comment_service.comment.model.dto.response.TreeCommentData;

import jakarta.annotation.Nullable;

public interface CommentQueryService {
    List<RootCommentData> getCommentsForPost(long postId, EventFilter eventFilter, @Nullable UUID requestingUser);
    List<TreeCommentData> getCommentTree(long commentId, @Nullable UUID requestingUser);
    TreeCommentData getComment(long commentId, @Nullable UUID requestingUser);
}
