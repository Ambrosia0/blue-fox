package com.ambrosia.community_service.community.repository.custom.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.repository.custom.CustomScopeLinkRepository;
import com.ambrosia.community_service.community.utils.ScopeEnum;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CustomScopeLinkRepositoryImpl implements CustomScopeLinkRepository {
    private final JdbcClient jdbcClient;

    @Override
    public List<CommunityScopeResponse> getCommunityUsersScopes(long communityId) {
        var sql = """
        SELECT
            sl.user_id,
            array_agg(sl.scope_id) as scopes
        FROM scope_link sl
        WHERE sl.community_id = :communityId
        GROUP BY sl.user_id
        """;
        return jdbcClient
            .sql(sql)
            .param("communityId", communityId)
            .query((rs, rowNum) -> new CommunityScopeResponse(
                rs.getObject("user_id", UUID.class), 
                Arrays.stream((Short[])rs.getArray("scopes").getArray())
                    .map(t -> ScopeEnum.fromId(t))
                    .toArray(ScopeEnum[]::new)
            ))
            .list();
    }

    @Override
    public Optional<CommunityScopeResponse> findByUserIdAndCommunityId(UUID userId, long communityId) {
        var sql = """
        SELECT
            sl.user_id,
            array_agg(sl.scope_id) as scopes
        FROM scope_link sl
        WHERE sl.community_id = :communityId
        AND sl.user_id = :userId
        GROUP BY sl.user_id
        """;
        return jdbcClient
            .sql(sql)
            .param("communityId", communityId)
            .param("userId", userId)
            .query((rs, rowNum) -> new CommunityScopeResponse(
                rs.getObject("user_id", UUID.class),
                Arrays.stream((Short[])rs.getArray("scopes").getArray())
                    .map(t -> ScopeEnum.fromId(t))
                    .toArray(ScopeEnum[]::new)
            ))
            .optional();
    }
}
