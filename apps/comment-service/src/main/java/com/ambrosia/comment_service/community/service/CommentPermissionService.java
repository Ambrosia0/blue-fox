package com.ambrosia.comment_service.community.service;

import com.ambrosia.comment_service.community.utils.CommentPolicy;

public interface CommentPermissionService {
    void validateCommentCreate(CommentPolicy commentPolicy, long postId);
    void validateCommentView(CommentPolicy commentPolicy, long postId);
    void validateCommentLike(CommentPolicy commentPolicy, long commentId);
    void validateCommentDelete(CommentPolicy commentPolicy, long commentId);
}
