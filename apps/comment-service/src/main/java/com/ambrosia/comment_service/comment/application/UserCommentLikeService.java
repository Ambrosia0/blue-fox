package com.ambrosia.comment_service.comment.application;

import com.ambrosia.library_policy.policy.Actor;

public interface UserCommentLikeService {
    void likeComment(long commentId, Actor actor);
    void unlikeComment(long commentId, Actor actor);
}
