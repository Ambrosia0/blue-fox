package com.ambrosia.content_service.like.repository.custom.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.like.model.entity.PostLikeKey;
import com.ambrosia.content_service.like.repository.custom.CustomPostLikeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CustomPostLikeRepositoryImpl implements CustomPostLikeRepository{
    private final JdbcTemplate jdbcTemplate;

    private final JdbcClient jdbcClient;

    public Map<Long, Long> batchSaveAll(Collection<PostLikeKey> postLike){
        if(postLike.isEmpty())
            return Map.of();
        var sql = """
        WITH inserted AS (
            INSERT INTO post_like(post_id, user_id) 
            SELECT post_id, user_id FROM unnest(?::bigint[], ?::uuid[]) as t(post_id, user_id)
            JOIN post ON post.id = post_id
            ON CONFLICT DO NOTHING
            RETURNING post_id
        ) 
        SELECT post_id, COUNT(*) as cnt FROM inserted GROUP BY post_id
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            var statement = conn.prepareStatement(sql);
            var postIds = postLike.stream().map(val -> val.getPostId()).toArray(Long[]::new);
            var userIds = postLike.stream().map(val -> val.getUserId()).toArray(UUID[]::new);
            statement.setArray(1, conn.createArrayOf("bigint", postIds));
            statement.setArray(2, conn.createArrayOf("uuid", userIds));
            try (var rs = statement.executeQuery()) {
                var resultMap = new HashMap<Long, Long>();
                while (rs.next()) {
                    resultMap.put(rs.getLong(1), rs.getLong(2));
                }
                return resultMap;
            }
        } catch (Exception e) {
            log.error("Can't execute batch save on likes!", e);
            return Map.of();
        }
    }

    public Map<Long, Long> batchDeleteAll(Collection<PostLikeKey> postLike){
        if(postLike.isEmpty())
            return Map.of();
        var sql = """
        WITH deleted AS (
            DELETE FROM post_like 
            WHERE (post_id, user_id) 
            IN (SELECT * FROM unnest(?::bigint[], ?::uuid[])) 
            RETURNING post_id
        )
        SELECT post_id, -COUNT(*) as cnt FROM deleted GROUP BY post_id
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            var statement = conn.prepareStatement(sql);
            var postIds = postLike.stream().map(val -> val.getPostId()).toArray(Long[]::new);
            var userIds = postLike.stream().map(val -> val.getUserId()).toArray(UUID[]::new);
            statement.setArray(1, conn.createArrayOf("bigint", postIds));
            statement.setArray(2, conn.createArrayOf("uuid", userIds));
            try (var rs = statement.executeQuery()) {
                var resultMap = new HashMap<Long, Long>();
                while (rs.next()) {
                    resultMap.put(rs.getLong(1), rs.getLong(2));
                }
                return resultMap;
            }
        } catch (Exception e) {
            log.error("Can't execute batch save on likes!", e);
            return Map.of();
        }
    }

    @Override
    public int returningDelete(UUID userId, long postId) {
        return jdbcClient
            .sql("DELETE FROM post_like WHERE user_id = ? AND post_id = ?")
            .param("userId", userId)
            .param("postId", postId)
            .update();
    }

    @Override
    public int saveWithoutCheck(UUID userId, long postId) {
        try {
            return jdbcClient
                .sql("INSERT INTO post_like(user_id, post_id) VALUES (:userId, :postId) ON CONFLICT DO NOTHING")
                .param("user_id", userId)
                .param("post_id", postId)
                .update();
        } catch (Exception e) {
            return 0;
        }
    }
}
