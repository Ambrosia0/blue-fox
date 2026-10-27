package com.ambrosia.report_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.report_service.community.repository.custom.CustomCommunityRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.community.entity.CommunityProjection;
import org.springframework.data.repository.Repository;

public interface CommunityProjectionRepository extends
        Repository<CommunityProjection, Long>,
        CustomCommunityRepository {
    @Query("SELECT * FROM community_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<CommunityProjection> findById(Long id);
    
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM community_projection
        WHERE id = :id
        AND deleted_at IS NULL
    )
    """)
    boolean existsById(Long id);
}
