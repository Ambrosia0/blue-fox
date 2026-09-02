package com.ambrosia.comment_service.attachment.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.comment_service.attachment.model.entity.CommentAttachment;
import com.ambrosia.comment_service.attachment.repository.custom.CustomAttachmentRepository;

public interface AttachmentRepository extends 
        CrudRepository<CommentAttachment, String>,
        CustomAttachmentRepository{
    @Query("""
    WITH claimed AS (
        SELECT * FROM comment_attachment ca
        WHERE ca.to_delete = 'true'
        AND (claimed_at IS NULL OR claimed_at < now() - interval '10 minutes')
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    )
    UPDATE comment_attachment ca
    SET claimed_at = CURRENT_TIMESTAMP
    FROM claimed c
    WHERE ca.attachment_id = c.attachment_id
    RETURNING ca.comment_id, ca.attachment_id
    """)
    List<CommentAttachment> findAllDeletable(long limit);
}
