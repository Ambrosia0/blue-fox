package com.ambrosia.comment_service.attachment.application;

import com.ambrosia.comment_service.attachment.api.dto.request.FileMetadata;
import com.ambrosia.comment_service.attachment.api.dto.response.AttachmentUploadResponse;

public interface AttachmentService {
    AttachmentUploadResponse attachMedia(String attachmentId, FileMetadata fileMetadata);
    void confirmAttachmentUpload(long commentId, String attachmentId);
}
