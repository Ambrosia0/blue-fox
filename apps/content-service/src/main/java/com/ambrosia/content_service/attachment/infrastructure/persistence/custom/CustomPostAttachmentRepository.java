package com.ambrosia.content_service.attachment.infrastructure.persistence.custom;

import java.util.List;

import com.ambrosia.content_service.attachment.domain.entity.PostAttachment;

public interface CustomPostAttachmentRepository {
    void batchDeleteAll(List<PostAttachment> toDelete);
}
