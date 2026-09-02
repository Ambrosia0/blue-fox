package com.ambrosia.comment_service.comment.service;

import java.util.UUID;

import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;

public interface UserCommentService {
    CreateCommentResponse createComment(UUID userId, CreateComment request);
    CreateCommentResponse confirmAttachmentUpload(UUID userId, long commentId, String attachmentId);
    boolean isExists(long commentId);
}
