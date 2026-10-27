package com.ambrosia.content_service.community.repository;

import java.util.Optional;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.community.repository.custom.CustomCommunityProjectionRepository;

public interface CommunityProjectionRepository extends 
        Repository<CommunityProjection, Long>,
        CustomCommunityProjectionRepository{
    @Query("SELECT is_private FROM community_projection WHERE id = :communityId")
    Optional<Boolean> findIsCommunityPrivate(Long communityId);

    @Query("SELECT * FROM community_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<CommunityProjection> findById(Long id);
    
    void deleteById(Long id);
    void deleteAll();
}
