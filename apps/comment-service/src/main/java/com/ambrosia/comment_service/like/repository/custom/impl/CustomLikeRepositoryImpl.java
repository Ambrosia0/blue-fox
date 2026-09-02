package com.ambrosia.comment_service.like.repository.custom.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.like.model.dto.LikeDelta;
import com.ambrosia.comment_service.like.model.entity.CommentLike;
import com.ambrosia.comment_service.like.repository.custom.CustomLikeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CustomLikeRepositoryImpl implements CustomLikeRepository {
    private final JdbcClient jdbcClient;

    private final JdbcTemplate jdbcTemplate;


    @Override
    public List<Long> getUserLikesForCommentTree(long commentId, UUID userId) {
        var query = """
                WITH RECURSIVE comment_tree AS(
                        SELECT comment_like.comment_id, comment.parent_comment_id FROM comment_like
                        JOIN comment ON comment.id = comment_id 
                        WHERE comment_like.user_id = :userId
                        AND comment_id = :commentId
                        AND comment.parent_comment_id IS NULL

                        UNION ALL

                        SELECT comment_like.comment_id, comment.parent_comment_id
                        FROM comment_like
                        JOIN comment ON comment.id = comment_id
                        JOIN comment_tree ON comment.parent_comment_id = comment_tree.comment_id
                    )

                SELECT id FROM comment_like;
                """;
        return jdbcClient
            .sql(query)
            .param("commentId", commentId)
            .param("userId", userId)
            .query(Long.class)
            .list();
    }
    @Override
    public List<Long> getUserLikesForPostComments(long postId, UUID userId) {
        var query = """
                SELECT comment_like.comment_id FROM comment_like
                JOIN comment ON comment.id = comment_id AND comment.post_id = :postId
                WHERE comment_like.user_id = :userId
                AND comment.parent_comment_id IS NULL;
                """;
        return jdbcClient
            .sql(query)
            .param("postId", postId)
            .param("userId", userId)
            .query(Long.class)
            .list();
    }

    @Override
    public List<LikeDelta> batchDeleteAll(Collection<CommentLike> commentLike) {
        var it = commentLike.iterator();
        if(!it.hasNext())
            return List.of();
        var sql = """
        WITH deleted AS (
            DELETE FROM comment_like 
            WHERE (comment_id, user_id) IN (SELECT * FROM unnest(?::bigint[], ?::uuid[])) 
            RETURNING comment_id
        )
        SELECT 
            c.post_id, 
            d.comment_id, 
            -COUNT(*) as cnt 
        FROM deleted d
        JOIN comment c ON c.id = d.comment_id 
        GROUP BY d.comment_id, c.post_id
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            var statement = conn.prepareStatement(sql);
            var commentIds = commentLike.stream().map(val -> val.getId().getCommentId()).toArray(Long[]::new);
            var userIds = commentLike.stream().map(val -> val.getId().getUserId()).toArray(UUID[]::new);
            statement.setArray(1, conn.createArrayOf("bigint", commentIds));
            statement.setArray(2, conn.createArrayOf("uuid", userIds));
            try (var rs = statement.executeQuery()) {
                var resultList = new ArrayList<LikeDelta>();
                while (rs.next()) {
                    resultList.add(LikeDelta.create(rs.getLong(1), rs.getLong(2), rs.getLong(3)));
                }
                return resultList;
            }
        } catch (Exception e) {
            log.error("Can't delete like entity on batch request (query with select from arrays as params)!", e);
            return List.of();
        }
    }


    // TEEEEEEEEEEESTTTT
    @Override
    public List<LikeDelta> batchSaveAll(Collection<CommentLike> commentLike) {
        var it = commentLike.iterator();
        if(!it.hasNext())
            return List.of();
        var sql = 
        """
        WITH inserted AS (
                INSERT INTO comment_like(comment_id, user_id) 
                SELECT t.comment_id, t.user_id FROM unnest(?::bigint[], ?::uuid[]) as t(comment_id, user_id)
                JOIN comment ON comment.id = comment_id
                ON CONFLICT DO NOTHING 
                RETURNING comment_id
            ) 
        SELECT 
            c.post_id, 
            i.comment_id, 
            COUNT(*) as cnt 
        FROM inserted i 
        JOIN comment c ON c.id = i.comment_id 
        GROUP BY i.comment_id, c.post_id
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            var statement = conn.prepareStatement(sql);
            var commentIds = commentLike.stream().map(val -> val.getId().getCommentId()).toArray(Long[]::new);
            var userIds = commentLike.stream().map(val -> val.getId().getUserId()).toArray(UUID[]::new);
            statement.setArray(1, conn.createArrayOf("bigint", commentIds));
            statement.setArray(2, conn.createArrayOf("uuid", userIds));
            try (var rs = statement.executeQuery()) {
                var resultList = new ArrayList<LikeDelta>();
                while(rs.next()){
                    resultList.add(LikeDelta.create(rs.getLong(1), rs.getLong(2), rs.getLong(3)));
                }
                return resultList;
            }
        } catch (Exception e) {
            log.error("Can't insert entity on batch request (query with select from arrays as params)!", e);
            return List.of();
        }   
    }

}
