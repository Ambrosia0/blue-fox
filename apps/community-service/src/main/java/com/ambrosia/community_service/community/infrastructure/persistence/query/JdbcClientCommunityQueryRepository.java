package com.ambrosia.community_service.community.infrastructure.persistence.query;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.application.query.CommunityQueryRepository;
import com.ambrosia.community_service.community.application.query.model.CommunityResponse;
import com.ambrosia.community_service.community.application.query.model.CommunityUserDataResponse;
import com.ambrosia.community_service.community.infrastructure.persistence.mapper.CommunityResponseRowMapper;
import com.ambrosia.community_service.community.utils.ScopeEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class JdbcClientCommunityQueryRepository implements CommunityQueryRepository{
    private final JdbcClient jdbcClient;

    private final CommunityResponseRowMapper communityResponseRowMapper;

    @Override
    public Optional<CommunityResponse> findBySlug(String slug) {
        var sql = """
            SELECT 
                c.*,
                COALESCE(
                    (
                        SELECT jsonb_agg(json_build_object(
                            'id', t.id,
                            'username', t.username,
                            'first_name', t.first_name,
                            'last_name', t.last_name,
                            'avatar_id', t.avatar_id
                        ))
                        FROM (
                            SELECT DISTINCT ON (up.id)
                                up.id, 
                                up.username, 
                                up.first_name, 
                                up.last_name, 
                                up.avatar_id    
                            FROM user_projection up
                            JOIN scope_link sl ON sl.user_id = up.id
                            WHERE sl.community_id = c.id
                        ) t
                    ), 
                    '[]'::jsonb
                )::text as community_moderators,
                up.id as user_id,
                up.username,
                up.first_name,
                up.last_name,
                up.avatar_id
            FROM community c
            LEFT JOIN user_projection up ON up.id = c.owner_id
            WHERE c.slug = ?
        """;
        return jdbcClient
            .sql(sql)
            .param(1, slug)
            .query(communityResponseRowMapper)
            .optional();
    }

    @Override
    public CommunityUserDataResponse findCommunityUserData(long communityId, UUID userId) {
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
            .query((rs, rowNum) -> new CommunityUserDataResponse(
                rs.getBoolean("is_followed"),
                Arrays.stream((Short[])rs.getArray("scopes").getArray())
                    .map(t -> ScopeEnum.fromId(t))
                    .toArray(ScopeEnum[]::new)
            ))
            .single();
    }
}
