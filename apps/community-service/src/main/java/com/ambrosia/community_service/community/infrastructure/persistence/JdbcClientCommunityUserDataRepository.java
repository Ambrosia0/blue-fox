package com.ambrosia.community_service.community.infrastructure.persistence;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.application.policy.CommunityUserDataRepository;
import com.ambrosia.community_service.community.domain.policy.entity.CommunityCreationUserContext;
import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.community.infrastructure.persistence.mapper.CommunityModeratorUserDataRowMapper;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityFollowUserContext;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class JdbcClientCommunityUserDataRepository implements CommunityUserDataRepository{
    private final JdbcClient jdbcClient;

    private final CommunityModeratorUserDataRowMapper communityModeratorUserDataRowMapper;
    
    @Override
    public Optional<CommunityUserContext> loadCommunityUserData(UUID userId, String slug) {
        var sql ="""
        SELECT
            EXISTS(
                SELECT 1 FROM community_ban cb
                WHERE cb.user_id = :userId
                AND cb.community_id = c.id
                AND (
                    cb.before_date IS NULL
                    OR cb.before_date > CURRENT_TIMESTAMP
                )
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_follow cf
                WHERE cf.user_id = :userId
                AND cf.community_id = c.id
            ) as is_followed,
            EXISTS (
                SELECT 1 FROM scope_link sl
                WHERE sl.community_id = c.id
                AND sl.user_id = :userId
            ) as is_moderator,
            c.is_private
        FROM community c
        WHERE c.slug = :slug
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("slug", slug)
            .query(CommunityUserContext.class)
            .optional();
    }

    @Override
    public Optional<CommunityUserContext> loadCommunityUserData(UUID userId, Long communityId) {
        var sql ="""
        SELECT
            EXISTS(
                SELECT 1 FROM community_ban cb
                WHERE cb.user_id = :userId
                AND cb.community_id = c.id
                AND (
                    cb.before_date IS NULL
                    OR cb.before_date > CURRENT_TIMESTAMP
                )
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_follow cf
                WHERE cf.user_id = :userId
                AND cf.community_id = c.id
            ) as is_followed,
            EXISTS (
                SELECT 1 FROM scope_link sl
                WHERE sl.community_id = c.id
                AND sl.user_id = :userId
            ) as is_moderator,
            c.is_private
        FROM community c
        WHERE c.id = :communityId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .query(CommunityUserContext.class)
            .optional();
    }

    @Override
    public Optional<CommunityFollowUserContext> loadFollowUserData(UUID userId, Long communityId) {
        var sql ="""
        SELECT
            EXISTS(
                SELECT 1 FROM community_ban cb
                WHERE cb.user_id = :userId
                AND cb.community_id = c.id
                AND (
                    cb.before_date IS NULL
                    OR cb.before_date > CURRENT_TIMESTAMP
                )
            ) as is_banned,
            EXISTS(
                SELECT 1 FROM community_follow cf
                WHERE cf.user_id = :userId
                AND cf.community_id = c.id
            ) as is_followed,
            EXISTS (
                SELECT 1 FROM scope_link sl
                WHERE sl.community_id = c.id
                AND sl.user_id = :userId
            ) as is_moderator,
            EXISTS (
                SELECT 1 FROM community_follow_request cfr
                WHERE cfr.user_id = :userId
                AND cfr.community_id = c.id
            ) as is_requested,
            c.is_private
        FROM community c
        WHERE c.id = :communityId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .query(CommunityFollowUserContext.class)
            .optional();
    }

    @Override
    public Optional<CommunityModeratorUserContext> loadModeratorUserData(UUID userId, Long communityId, @Nullable UUID targetUser) {
        var sql = """
        SELECT
            COALESCE(
                array_agg(sl.scope_id),
                '{}'
            ) as permissions,
            EXISTS(
                SELECT 1 FROM scope_link sl
                WHERE sl.community_id = c.id
                AND sl.user_id = :targetUser
            ) as is_target_moderator,
            EXISTS(
                SELECT 1 FROM user_projection up
                WHERE up.id = :targetUser
            ) as is_target_exist,
            EXISTS(
                SELECT 1 FROM community_ban cb
                WHERE cb.user_id = :targetUser
                AND cb.community_id = c.id
                AND (
                    cb.before_date IS NULL
                    OR cb.before_date > CURRENT_TIMESTAMP
                )
            ) as is_target_banned,
            EXISTS (
                SELECT 1 FROM community_follow cf
                WHERE cf.user_id = :targetUser
                AND community_id = c.id
            )
        FROM community c
        LEFT JOIN scope_link sl ON sl.community_id = c.id AND sl.user_id = :userId
        WHERE c.id = :communityId
        GROUP BY c.id
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .param("targetUser", targetUser)
            .query(communityModeratorUserDataRowMapper)
            .optional();
    }

    @Override
    public CommunityCreationUserContext loadCreationUserData(UUID userId) {
        var sql ="""
        SELECT COUNT(*) as owned_communities
        FROM community c
        WHERE c.owner_id = :userId
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .query(CommunityCreationUserContext.class)
            .single();
    }

    @Override
    public Optional<CommunityEditUserContext> loadEditUserData(UUID userId, Long communityId, @Nullable Set<UUID> targetUsers) {
        String sql;
        if(targetUsers != null && !targetUsers.isEmpty()){
            sql = """
            SELECT
                c.owner_id,
                EXISTS(
                    SELECT 1 FROM community_ban cb
                    WHERE cb.user_id IN ( :targetUsers )
                    AND cb.community_id = c.id
                    AND (
                        cb.before_date IS NULL OR
                        cb.before_date > CURRENT_TIMESTAMP
                    )

                ) as is_any_target_banned,
                (
                    SELECT COUNT(*) = :size
                    FROM user_projection up
                    WHERE up.id IN ( :targetUsers )
                ) as is_all_targets_exists
            FROM community c
            WHERE c.id = :communityId
            """;
        }else{
            sql = """
            SELECT
                c.owner_id,
                false as is_any_target_banned,
                true as is_all_targets_exists
            FROM community c
            WHERE c.id = :communityId   
            """;
        }
        
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .param("targetUsers", targetUsers)
            .param("size", targetUsers != null? targetUsers.size(): null)
            .query(CommunityEditUserContext.class)
            .optional();
    }
}
