package com.ambrosia.comment_service.comment.service;

import java.util.UUID;

public interface UserCommentLikeService {
    void likeComment(long commentId, UUID userId);
    void unlikeComment(long commentId, UUID userId);
}
