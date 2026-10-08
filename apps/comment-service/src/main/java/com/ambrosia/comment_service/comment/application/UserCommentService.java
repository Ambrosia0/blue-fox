package com.ambrosia.comment_service.comment.application;

import com.ambrosia.comment_service.comment.api.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.api.dto.response.CreateCommentResponse;
import com.ambrosia.library_policy.policy.Actor;

public interface UserCommentService {
    CreateCommentResponse createComment(
        Actor actor, 
        CreateComment request
    );
    CreateCommentResponse confirmAttachmentUpload(
        Actor actor, 
        long commentId, 
        String attachmentId
    );
    void deleteComment(
        long commentId,
        Actor actor
    );
    boolean isExists(long commentId);
}
