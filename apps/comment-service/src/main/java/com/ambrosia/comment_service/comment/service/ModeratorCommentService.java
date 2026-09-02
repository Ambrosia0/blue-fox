package com.ambrosia.comment_service.comment.service;

import java.util.UUID;

public interface ModeratorCommentService {
    void deleteComment(UUID requestingUser, long commentId);
}
