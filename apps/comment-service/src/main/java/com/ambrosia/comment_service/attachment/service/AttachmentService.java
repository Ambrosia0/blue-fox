package com.ambrosia.comment_service.attachment.service;

import com.ambrosia.comment_service.attachment.model.dto.request.FileMetadata;
import com.ambrosia.comment_service.attachment.model.dto.response.AttachmentUploadResponse;

public interface AttachmentService {
    AttachmentUploadResponse attachMedia(String attachmentId, FileMetadata fileMetadata);
    void confirmAttachmentUpload(long commentId, String attachmentId);
}
