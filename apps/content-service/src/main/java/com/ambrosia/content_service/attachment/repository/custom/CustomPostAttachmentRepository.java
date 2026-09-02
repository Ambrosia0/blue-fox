package com.ambrosia.content_service.attachment.repository.custom;

import java.util.List;

import com.ambrosia.content_service.attachment.model.entity.PostAttachment;

public interface CustomPostAttachmentRepository {
    void batchDeleteAll(List<PostAttachment> toDelete);
}
