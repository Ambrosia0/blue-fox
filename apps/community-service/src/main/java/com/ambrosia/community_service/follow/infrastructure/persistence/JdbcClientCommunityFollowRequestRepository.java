package com.ambrosia.community_service.follow.infrastructure.persistence;

import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRequestRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcClientCommunityFollowRequestRepository implements CommunityFollowRequestRepository{
    private final JdbcClient jdbcClient;

    @Override
    public boolean approve(UUID userId, Long communityId) {
        var sql = """
        WITH deleted AS (
            DELETE FROM community_follow_request
            WHERE user_id = :userId
            AND community_id = :communityId
        )
        INSERT INTO community_follow(user_id, community_id)
        VALUES (:userId, :communityId)
        """;

        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .update() > 0;
    }

    @Override
    public boolean decline(UUID userId, Long communityId) {
        var sql ="""
        DELETE FROM community_follow_request
        WHERE user_id = :userId
        AND community_id = :communityId
        """;

        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .update() > 0;
    }
}
