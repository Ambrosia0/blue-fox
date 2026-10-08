package com.ambrosia.community_service.follow.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRepository;

import lombok.RequiredArgsConstructor;

@Repository 
@RequiredArgsConstructor 
public class JdbcClientCommunityFollowRepository implements CommunityFollowRepository{
    private final JdbcClient jdbcClient;
    
    @Override
    public void persist(CommunityFollow communityFollow) {
        Assert.notNull(communityFollow.getState(), "State of the follow must no be null!");
        String sql = null;

        switch (communityFollow.getState()) {
            case FOLLOWED -> {
                sql ="""
                WITH deleted AS (
                    DELETE FROM community_follow_request
                    WHERE user_id = :userId
                    AND community_id = :communityId
                )  
                INSERT INTO community_follow(user_id, community_id)
                VALUES(:userId, :communityId)
                ON CONFLICT(user_id, community_id) DO NOTHING
                """;
            }
            case REQUESTED ->{
                sql = """
                INSERT INTO community_follow_request(user_id, community_id)
                VALUES (:userId, :communityId)
                ON CONFLICT(user_id, community_id) DO NOTHING
                """;
            }
            case UNFOLLOWED -> {
                sql = """
                WITH deleted AS (
                    DELETE FROM community_follow_request
                    WHERE user_id = :userId
                    AND community_id = :communityId
                )
                DELETE FROM community_follow
                WHERE user_id = :userId
                AND community_id = :communityId        
                """;
            }
        }

        jdbcClient
            .sql(sql)
            .param("userId", communityFollow.getUserId())
            .param("communityId", communityFollow.getCommunityId())
            .update();
    }

    @Override
    public boolean remove(UUID userId, Long communityId) {
        var sql = """
        WITH deleted AS (
            DELETE FROM community_follow_request cfr
            WHERE cfr.user_id = :userId
            AND cfr.community_id = :communityId
        )
        DELETE FROM community_follow cf
        WHERE cf.user_id = :userId
        AND cf.community_id = :communityId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .update() > 0;
    }

    @Override
    public Optional<CommunityFollow> findById(UUID userId, Long communityId) {
        var sql ="""
        SELECT 
            cf.user_id,
            cf.community_id,
            cf.followed_at,
            'FOLLOWED' as state
        FROM community_follow cf
        WHERE user_id = :userId
        AND community_id = :communityId

        UNION ALL

        SELECT
            cfr.user_id,
            cfr.community_id,
            cfr.created_at,
            'REQUESTED' as state
        FROM community_follow_request cfr
        WHERE user_id = :userId
        AND community_id = :communityId
        """;

        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .query(CommunityFollow.class)
            .optional();
    }
}
