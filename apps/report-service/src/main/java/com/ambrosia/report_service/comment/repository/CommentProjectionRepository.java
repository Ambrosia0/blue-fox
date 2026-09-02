package com.ambrosia.report_service.comment.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.comment.entity.CommentProjection;

public interface CommentProjectionRepository extends CrudRepository<CommentProjection, Long> {
    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO comment_projection(id)
    SELECT :#{#commentProjection.id}
    FROM inserted
    RETURNING id
    """
    )
    CommentProjection insert(CommentProjection commentProjection, UUID eventId);

    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    DELETE FROM comment_projection cp
    USING inserted i
    WHERE cp.id = :commentId
    """)
    void delete(Long commentId, UUID eventId);
}
