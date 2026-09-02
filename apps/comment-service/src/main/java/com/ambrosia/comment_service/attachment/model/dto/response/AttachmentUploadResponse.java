package com.ambrosia.comment_service.attachment.model.dto.response;

public record AttachmentUploadResponse(
    String uploadUrl,
    String attachmentId
) {
    public static AttachmentUploadResponse from(String uploadUrl, String attachmentId){
        return new AttachmentUploadResponse(uploadUrl, attachmentId);
    }
}
