package com.ambrosia.comment_service.attachment.repository.custom;

import java.util.List;

import com.ambrosia.comment_service.attachment.model.entity.CommentAttachment;

public interface CustomAttachmentRepository {
    void batchDelete(List<CommentAttachment> toDelete);
}
