package com.ambrosia.report_service.comment.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.report_service.comment.repository.custom.CustomCommentRepository;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.report_service.comment.entity.CommentProjection;
import org.springframework.data.repository.Repository;

public interface CommentProjectionRepository extends
        Repository<CommentProjection, Long>,
        CustomCommentRepository {
    @Query("SELECT * FROM comment_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<CommentProjection> findById(Long id);
    
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM comment_projection
        WHERE id = :id
        AND deleted_at IS NULL
    )
    """)
    boolean existsById(Long id);
}
