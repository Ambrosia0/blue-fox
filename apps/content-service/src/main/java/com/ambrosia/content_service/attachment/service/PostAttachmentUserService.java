package com.ambrosia.content_service.attachment.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.content_service.attachment.model.dto.request.FileMetadata;
import com.ambrosia.content_service.attachment.model.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.model.entity.PostAttachment;

public interface PostAttachmentUserService {
    AttachmentUploadResponse uploadAttachment(UUID userId, long postId, FileMetadata fileMetadata);
    void validateAttachmentUpload(UUID userId, long postId, String attachmentId);
    void deleteAttachment(UUID requestingUser, long postId, String attachmentId);
    List<PostAttachment> getAttachments(UUID requestingUser, long postId);
}
