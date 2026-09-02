package com.ambrosia.community_service.community.repository.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityUserData;
import com.ambrosia.community_service.community.repository.CommunityQueryRepository;
import com.ambrosia.community_service.community.repository.mapper.CommunityResponseRowMapper;
import com.ambrosia.community_service.community.utils.ScopeEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CommunityQueryRepositoryImpl implements CommunityQueryRepository{
    private final JdbcClient jdbcClient;

    private final CommunityResponseRowMapper communityResponseRowMapper;

    @Cacheable(cacheNames = "community", key = "#slug")
    @Override
    public Optional<CommunityResponse> findBySlug(String slug) {
        var sql = """
            SELECT
                c.*,
                array_agg(DISTINCT sl.user_id) as community_moderators
            FROM community c
            LEFT JOIN scope_link sl ON sl.community_id = c.id
            WHERE c.slug = ?
            GROUP BY c.id
        """;
        return jdbcClient
            .sql(sql)
            .param(1, slug)
            .query(communityResponseRowMapper)
            .optional();
    }

    @Cacheable(cacheNames = "community-user-data")
    @Override
    public CommunityUserData findCommunityUserData(long communityId, UUID userId) {
        var sql = """
        SELECT
            EXISTS(
                SELECT 1 FROM community_follow cf 
                WHERE cf.user_id = :userId 
                AND community_id = :communityId
            ) as is_followed,
            COALESCE(
                ARRAY(
                    SELECT sl.scope_id
                    FROM scope_link sl
                    WHERE sl.user_id = :userId
                    AND sl.community_id = :communityId
                ),
                '{}'
            ) as scopes
        """;
        return jdbcClient
            .sql(sql)
            .param("userId", userId)
            .param("communityId", communityId)
            .query((rs, rowNum) -> new CommunityUserData(
                rs.getBoolean("is_followed"),
                Arrays.stream((Short[])rs.getArray("scopes").getArray())
                    .map(t -> ScopeEnum.fromId(t))
                    .toArray(ScopeEnum[]::new)
            ))
            .single();
    }
}
