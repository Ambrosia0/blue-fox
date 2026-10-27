package com.ambrosia.report_service.post.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.report_service.post.repository.custom.CustomPostRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.post.entity.PostProjection;
import org.springframework.data.repository.Repository;

public interface PostProjectionRepository extends
        Repository<PostProjection, Long>,
        CustomPostRepository {
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM post_projection
            WHERE id = :id
            AND is_published IS true
            AND deleted_at IS NULL
    )
    """)
    boolean existsById(Long id);
    
    @Query("SELECT * FROM post_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<PostProjection> findById(Long id);
    
}
