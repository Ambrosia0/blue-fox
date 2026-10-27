package com.ambrosia.comment_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.custom.CustomCommunityProjectionRepository;

public interface CommunityProjectionRepository extends
        Repository<CommunityProjection, Long>,
        CustomCommunityProjectionRepository{
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
    WHERE pp.post_id = :postId AND deleted_at IS NULL
    """)
    Optional<CommunityProjection> findByPostId(long postId);

    @Query("""
    SELECT cp.id, cp.is_private FROM comment c
    JOIN post_projection pp ON pp.post_id = c.post_id
    JOIN community_projection cp ON cp.id = pp.community_id
    WHERE c.id = :commentId
    """)
    Optional<CommunityProjection> findByCommentId(long commentId);

    @Query("SELECT * FROM community_projection WHERE id = :id AND deleted_at IS NULL")
    Optional<CommunityProjection> findById(Long id);
    void deleteAll();
}
