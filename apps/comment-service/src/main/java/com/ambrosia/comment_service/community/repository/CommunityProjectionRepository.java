package com.ambrosia.comment_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.comment_service.community.model.entity.CommunityProjection;

public interface CommunityProjectionRepository extends CrudRepository<CommunityProjection, Long>{

    @Query("""
    SELECT cp.is_private FROM comment c
    JOIN post_projection pp ON pp.post_id = c.post_id
    JOIN community_projection cp ON cp.id = pp.community_id
    WHERE c.id = :commentId
    """)
    Optional<Boolean> isCommunityPrivateByCommentId(long commentId);

    @Query("""
    SELECT cp.is_private FROM post_projection pp
    JOIN community_projection cp ON cp.id = pp.community_id
    WHERE pp.post_id = :postId
    """)
    Optional<Boolean> isCommunityPrivateByPostId(long postId);
    
    @Query("""
    SELECT cp.id, cp.is_private FROM community_projection cp
    JOIN post_projection pp ON pp.community_id = cp.id
    WHERE pp.post_id = :postId
    """)
    Optional<CommunityProjection> findByPostId(long postId);

    @Query("""
    SELECT cp.id, cp.is_private FROM comment c
    JOIN post_projection pp ON pp.post_id = c.post_id
    JOIN community_projection cp ON cp.id = pp.community_id
    WHERE c.id = :commentId
    """)
    Optional<CommunityProjection> findByCommentId(long commentId);

    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO community_projection(id)
    SELECT :#{#communityProjection.id}
    FROM inserted
    RETURNING *
    """
    )
    CommunityProjection insert(CommunityProjection communityProjection, UUID eventId);

    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    DELETE FROM community_projection cp
    USING inserted i
    WHERE cp.id = :communityId
    """)
    void delete(Long communityId, UUID eventId);
}
