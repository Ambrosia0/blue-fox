package com.ambrosia.content_service.attachment.application;

import java.util.List;

import com.ambrosia.content_service.attachment.api.dto.request.FileMetadata;
import com.ambrosia.content_service.attachment.api.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.domain.entity.PostAttachment;
import com.ambrosia.library_policy.policy.Actor;

public interface PostAttachmentUserService {
    AttachmentUploadResponse uploadAttachment(Actor actor, long postId, FileMetadata fileMetadata);
    void validateAttachmentUpload(Actor actor, long postId, String attachmentId);
    void deleteAttachment(Actor actor, long postId, String attachmentId);
    List<PostAttachment> getAttachments(Actor actor, long postId);
}
