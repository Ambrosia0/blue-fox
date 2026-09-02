package com.ambrosia.report_service.post.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.post.entity.PostProjection;

public interface PostProjectionRepository extends CrudRepository<PostProjection, Long>{
    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO post_projection(id)
    SELECT :#{#postProjection.id}
    FROM inserted
    RETURNING id
    """)
    PostProjection insert(PostProjection postProjection, UUID eventId);

    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    DELETE FROM post_projection pp
    USING inserted i
    WHERE pp.id = :postId
    """)
    void delete(Long postId, UUID eventId);
}
