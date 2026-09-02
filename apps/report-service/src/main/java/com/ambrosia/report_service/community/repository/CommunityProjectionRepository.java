package com.ambrosia.report_service.community.repository;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.community.entity.CommunityProjection;

public interface CommunityProjectionRepository extends CrudRepository<CommunityProjection, Long>{
    @Query("""
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (:eventId)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO community_projection(id)
    SELECT :#{#communityProjection.id}
    FROM inserted
    RETURNING id
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
