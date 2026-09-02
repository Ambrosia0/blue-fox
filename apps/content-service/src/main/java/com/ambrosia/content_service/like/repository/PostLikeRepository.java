package com.ambrosia.content_service.like.repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ambrosia.content_service.like.model.entity.PostLike;
import com.ambrosia.content_service.like.model.entity.PostLikeKey;
import com.ambrosia.content_service.like.repository.custom.CustomPostLikeRepository;

public interface PostLikeRepository extends CrudRepository<PostLike, PostLikeKey>, CustomPostLikeRepository{
    @Query("""
            SELECT EXISTS(
                SELECT 1 FROM post_like
                WHERE post_id = :postId AND user_id = :userId
            )
            """)
    boolean existsById(@Param("userId") UUID userId, @Param("postId") long postId);
    
    @Modifying
    @Query("DELETE FROM post_like WHERE user_id = :userId AND post_id = :postId")
    void deleteById(@Param("userId") UUID userId, @Param("postId") long postId);

    @Query("SELECT * FROM post_like WHERE user_id = :userId AND post_id = :postId")
    PostLike findById(@Param("userId") UUID userId, @Param("postId") long postId);

    @Query("SELECT post_id FROM post_like WHERE user_id = :requestingUser AND post_id IN (:postIds)")
    Set<Long> findLikedPostIdsByPostIds(List<Long> postIds, UUID requestingUser);
}
