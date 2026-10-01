package com.ambrosia.comment_service.comment.service;

import com.ambrosia.comment_service.community.utils.CommentPolicy;

public interface UserCommentLikeService {
    void likeComment(long commentId, CommentPolicy commentPolicy);
    void unlikeComment(long commentId, CommentPolicy commentPolicy);
}
