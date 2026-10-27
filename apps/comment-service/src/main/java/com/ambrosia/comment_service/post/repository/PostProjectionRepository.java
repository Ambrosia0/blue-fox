package com.ambrosia.comment_service.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.comment_service.comment.api.dto.PostCommentTuple;
import com.ambrosia.comment_service.post.model.entity.PostProjection;
import org.springframework.data.repository.Repository;


public interface PostProjectionRepository extends
        Repository<PostProjection, Long>,
        CustomPostProjectionRepository{
    
    @Query("SELECT * FROM post_projection WHERE post_id = :id AND deleted_at IS NULL")
    Optional<PostProjection> findById(Long id);
    
    @Query("""
    SELECT EXISTS(
        SELECT 1 FROM post_projection
        WHERE post_id = :id
        AND deleted_at IS NULL
    )
    """)
    boolean existsById(Long id);
    
    void deleteById(Long id);
}
