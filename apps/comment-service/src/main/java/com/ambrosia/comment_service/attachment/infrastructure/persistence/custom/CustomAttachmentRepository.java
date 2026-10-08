package com.ambrosia.comment_service.attachment.infrastructure.persistence.custom;

import java.util.List;

import com.ambrosia.comment_service.attachment.infrastructure.entity.CommentAttachment;

public interface CustomAttachmentRepository {
    void batchDelete(List<CommentAttachment> toDelete);
}
