package com.ambrosia.content_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.community.repository.custom.CustomCommunityProjectionRepository;

public interface CommunityProjectionRepository extends 
        Repository<CommunityProjection, Long>,
        CustomCommunityProjectionRepository{
    @Query("SELECT is_private FROM community_projection WHERE id = :communityId")
    Optional<Boolean> findIsCommunityPrivate(Long communityId);

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

    Optional<CommunityProjection> findById(Long id);
    
    void deleteAll();
}
