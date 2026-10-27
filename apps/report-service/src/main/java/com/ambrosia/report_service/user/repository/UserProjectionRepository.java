package com.ambrosia.report_service.user.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.report_service.user.repository.custom.CustomUserRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.report_service.user.entity.UserProjection;

public interface UserProjectionRepository extends
        Repository<UserProjection, UUID>,
        CustomUserRepository {
    
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM user_projection
        WHERE id = :id
        AND deleted_at IS NULL
    )
    """)
    boolean existsById(UUID id);
    
    @Query("SELECT * FROM user_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<UserProjection> findById(UUID id);
}