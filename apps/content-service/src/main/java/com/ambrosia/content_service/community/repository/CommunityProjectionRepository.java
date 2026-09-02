package com.ambrosia.content_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;

public interface CommunityProjectionRepository extends CrudRepository<CommunityProjection, Long>{
    @Query("SELECT is_private FROM community_projection WHERE id = :communityId")
    Optional<Boolean> findIsCommunityPrivate(Long communityId);

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
    UPDATE community_projection cp
    SET name = :#{#communityProjection.name},
        is_private = :#{#communityProjection.isPrivate},
        avatar_id = :#{#communityProjection.avatarId}
    FROM inserted
    WHERE cp.id = :#{#communityProjection.id}
    RETURNING cp.*
    """
    )
    CommunityProjection update(CommunityProjection communityProjection, UUID eventId);

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
