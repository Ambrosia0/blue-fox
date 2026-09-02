package com.ambrosia.comment_service.community.repository.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.community.model.dto.CommunityUserData;
import com.ambrosia.comment_service.community.repository.CommunityQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CommunityQueryRepositoryImpl implements CommunityQueryRepository{
    private final JdbcClient jdbcClient;

    @Override
    public Optional<CommunityUserData> findCommunityUserDataByPostId(long postId, UUID userId) {
        var sql = """
        SELECT 
            cp.id,
            cp.is_private as is_community_private,
            EXISTS(
                SELECT 1 FROM community_follow_projection cf
                WHERE cf.user_id = :userId 
                AND cf.community_id = cp.id 
            ) as is_followed,
            EXISTS (
                SELECT 1 FROM community_ban_projection cb
                WHERE cb.user_id = :userId
                AND cb.community_id = cp.id
            ) as is_banned
        FROM post_projection pp
        JOIN community_projection cp ON cp.id = pp.community_id
        WHERE pp.post_id = :postId
        """;
        return jdbcClient
            .sql(sql)
            .param("postId", postId)
            .param("userId", userId)
            .query(CommunityUserData.class)
            .optional();
    }

    @Override
    public Optional<CommunityUserData> findCommunityUserDataByCommentId(long commentId, UUID userId) {
        var sql = """
        SELECT 
            cp.id,
            cp.is_private as is_community_private,
            EXISTS(
                SELECT 1 FROM community_follow_projection cf
                WHERE cf.user_id = :userId 
                AND cf.community_id = cp.id 
            ) as is_followed,
            EXISTS (
                SELECT 1 FROM community_ban_projection cb
                WHERE cb.user_id = :userId
                AND cb.community_id = cp.id
            ) as is_banned
        FROM comment c
        JOIN post_projection pp ON pp.post_id = c.post_id
        JOIN community_projection cp ON pp.community_id = cp.id
        WHERE c.id = :commentId
        """;
        return jdbcClient
            .sql(sql)
            .param("commentId", commentId)
            .param("userId", userId)
            .query(CommunityUserData.class)
            .optional();
    }
}
