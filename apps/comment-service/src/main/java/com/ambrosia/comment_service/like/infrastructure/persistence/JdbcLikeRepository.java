package com.ambrosia.comment_service.like.infrastructure.persistence;

import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.Repository;

import com.ambrosia.comment_service.like.domain.repository.LikeRepository;
import com.ambrosia.comment_service.like.infrastructure.entity.CommentLike;
import com.ambrosia.comment_service.like.infrastructure.entity.keys.CommentLikeKey;
import com.ambrosia.comment_service.like.infrastructure.persistence.custom.CustomLikeRepository;

public interface JdbcLikeRepository extends 
        Repository<CommentLike, CommentLikeKey>, 
        CustomLikeRepository,
        LikeRepository{

    @Query("""
    INSERT INTO comment_like(comment_id, user_id) 
    VALUES(:commentId, :userId)        
    """)
    @Modifying 
    void add(UUID userId, Long commentId);

    @Query("""
    DELETE FROM comment_like
    WHERE comment_id = :commentId
    AND user_id = :userId
    """)
    @Modifying 
    void remove(UUID userId, Long commentId);

    void deleteAll();
}
