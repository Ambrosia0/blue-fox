package com.ambrosia.comment_service.post.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.ambrosia.comment_service.comment.model.dto.PostCommentTuple;
import com.ambrosia.comment_service.post.model.entity.PostProjection;


@CacheConfig(cacheNames = "post_projection")
public interface PostProjectionRepository extends 
        CrudRepository<PostProjection, Long>,
        CustomPostProjectionRepository{
    @Modifying
    @Query("UPDATE post_projection SET comment_count = comment_count+1 WHERE post_id = :postId")
    void incrementCounterById(long postId);

    List<PostCommentTuple> findAllByIdIn(List<Long> ids);

    @Modifying
    @Query("UPDATE post_projection SET is_visible = :isVisible WHERE post_id = :postId")
    long updateVisibility(long postId, boolean isVisible);
    
    @Modifying
    @Query("UPDATE post_projection SET community_id = NULL WHERE community_id = :communityId")
    long deleteCommunityId(long communityId);

    @Override
    Optional<PostProjection> findById(Long id);
    
    @Override
    void deleteById(Long id);
}
