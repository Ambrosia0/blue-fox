package com.ambrosia.content_service.community.repository.impl;

import java.util.Optional;
import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;
import com.ambrosia.content_service.community.repository.CommunityQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class CommunityQueryRepositoryImpl implements CommunityQueryRepository{
    private final JdbcClient jdbcClient;

    @Override
    public Optional<CommunityUserData> findCommunityUserDataByCommunityId(long communityId, UUID userId) {
        var sql = """
        SELECT
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
        FROM community_projection cp
        WHERE cp.id = :communityId
        """;
        return jdbcClient
            .sql(sql)
            .param("communityId", communityId)
            .param("userId", userId)
            .query(CommunityUserData.class)
            .optional();
    }
}
