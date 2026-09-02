package com.ambrosia.comment_service.comment.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.model.entity.Comment;
import com.ambrosia.comment_service.comment.repository.custom.CustomCommentRepository;

public interface CommentRepository extends 
    CrudRepository<Comment, Long>,
    CustomCommentRepository{
    int countByPostId(long postId);
    boolean existsByPostId(long postId);

    @Query("SELECT comment.post_id FROM comment WHERE comment.id = :commentId")
    Optional<Long> getPostId(long commentId);
    
    @Modifying
    @Query("UPDATE comment SET user_id = NULL, content = NULL, like_count = NULL, is_visible = false WHERE id = :commentId")
    long hideCommentById(@Param("commentId") long commentId);

    @Query("""
        SELECT community_id FROM comment c
        JOIN post_projection pp ON pp.post_id = c.post_id AND pp.community_id IS NOT NULL
        WHERE c.id = :commentId
    """)
    Optional<Long> findRelatedProjectionCommunityId(@Param("commentId") long commentId);
    
    boolean existsByIdAndIsVisibleIsTrue(long id);

    @Query("""
    WITH inserted_comment AS ( 
        INSERT INTO comment(post_id, user_id, content, parent_comment_id, like_count, number_of_children, is_visible) 
        SELECT p.id, :#{#comment.userId}, :#{#comment.content}, :#{#comment.parentCommentId}, :#{#comment.likeCount}, :#{#comment.numberOfChildren}, :#{#comment.isVisible} 
        FROM (SELECT :#{#comment.postId} AS id) p 
        JOIN post_projection ON post_projection.post_id = p.id
        WHERE (
            :#{#comment.parentCommentId} IS NULL 
            OR EXISTS (SELECT 1 FROM comment WHERE comment.id = :#{#comment.parentCommentId} AND comment.is_visible IS TRUE))
        RETURNING *
    ),
    updated_comments AS (
        WITH RECURSIVE comment_tree AS (
            SELECT id, parent_comment_id FROM inserted_comment
            
            UNION ALL

            SELECT c.id, c.parent_comment_id FROM comment c
            JOIN comment_tree ct ON ct.parent_comment_id = c.id
        )
        UPDATE comment SET number_of_children = number_of_children + 1 
        WHERE id IN (SELECT id FROM comment_tree) AND id != (SELECT id FROM inserted_comment)
    )
    SELECT * FROM inserted_comment;     
    """)
    Optional<CreateCommentResponse> insert(Comment comment);

    @Query("SELECT * FROM comment c WHERE c.id = :commentId AND user_id = :userId")
    Optional<CreateCommentResponse> findCreateProjection(long commentId, UUID userId);
}
