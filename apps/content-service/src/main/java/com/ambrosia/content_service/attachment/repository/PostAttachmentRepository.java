package com.ambrosia.content_service.attachment.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.content_service.attachment.model.entity.PostAttachment;
import com.ambrosia.content_service.attachment.repository.custom.CustomPostAttachmentRepository;

public interface PostAttachmentRepository extends 
        CrudRepository<PostAttachment, Long>,
        CustomPostAttachmentRepository{
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM post_attachment pa
        JOIN post p ON p.id = pa.post_id
        WHERE pa.attachment_id = :attachmentId
        AND p.author_id = :requestingUser
    )
    """)
    boolean existsByAuthorIdAndAttachmentId(UUID requestingUser, String attachmentId);

    @Query("""
    SELECT attachment_id
    FROM post_attachment pa
    JOIN post p ON p.id = pa.post_id
    WHERE p.id =:postId AND p.author_id = :authorId
    """)
    List<String> findAttachmentIdsByPostIdAndAuthorId(Long postId, UUID authorId);

    @Query("""
    WITH to_delete AS(
        SELECT pa.post_id, pa.attachment_id
        FROM post_attachment pa
        JOIN post p ON p.id = pa.post_id
        WHERE p.post_id = :postId 
            AND p.author_id = :authorId 
            AND pa_attachment_id = :attachmentId
    ),
    deleted AS(
    DELETE FROM post_attachment pa
        USING to_delete td
        WHERE pa.post_id = td.post_id 
        AND pa.attachment_id = td.attachment_id
    )
    SELECT EXISTS (
        SELECT 1 FROM deleted
    ) as deleted
    """)
    boolean deleteByAuthorIdAndAttachmentIdAndPostId(UUID authorId, String attachmentId, Long postId);
    

    @Modifying
    @Query("""
    UPDATE post_attachment pa
        SET to_delete = 'true'
        FROM post p
        WHERE p.id = pa.post_id
        AND pa.post_id = :postId 
        AND pa.attachment_id = :attachmentId
        AND p.author_id = :authorId
        AND to_delete = 'false'
    """)
    int deletionMark(UUID authorId, Long postId, String attachmentId);
    
    @Modifying
    @Query("""
    UPDATE post_attachment pa
        SET to_delete = 'true'
        FROM post p
        WHERE p.id = pa.post_id
        AND pa.post_id = :postId
        AND to_delete = 'false'
    
    """)
    int deletionMarkAll(UUID authorId, Long postId);

    @Query("""
    WITH claimed AS (
        SELECT * FROM post_attachment pa
        WHERE pa.to_delete = 'true'
        AND (claimed_at IS NULL OR claimed_at < now() - interval '10 minutes')
        LIMIT :limit
        FOR UPDATE SKIP LOCKED
    )
    UPDATE post_attachment pa
    SET claimed_at = CURRENT_TIMESTAMP
    FROM claimed c
    WHERE pa.id = c.id AND pa.attachment_id = c.attachment_id
    RETURNING pa.post_id, pa.attachment_id
    """)
    List<PostAttachment> findAllDeletable(long limit);

    List<PostAttachment> findByPostId(Long postId);
}
