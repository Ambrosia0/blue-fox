package com.ambrosia.comment_service.community.repository.custom.impl;

import java.util.UUID;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.comment_service.community.model.entity.CommunityPermission;
import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.custom.CustomCommunityProjectionRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Repository 
public class CustomCommunityProjectionRepositoryImpl implements CustomCommunityProjectionRepository{
    private final JdbcClient jdbcClient;

    @Override
    public void insert(CommunityProjection communityProjection, UUID eventId) {
        var sql = """
        WITH inserted AS (
            INSERT INTO processed_events(id) VALUES (:eventId)
            ON CONFLICT(id) DO NOTHING
            RETURNING id
        ),
        inserted_community AS (
            INSERT INTO community_projection(id, is_private)
            SELECT :communityId, :isPrivate
            FROM inserted
            RETURNING *
        )
        INSERT INTO community_permission(community_id, user_id, permission)
        SELECT ic.id, x.user_id, x.permission
        FROM inserted_community ic
        CROSS JOIN unnest(
            :userIds::uuid[],
            :permissions::text[]
        ) AS x(user_id, permission)
        """;

        var size = communityProjection.getPermissions().size();
        var userIds = new UUID[size];
        var permissions = new String[size];
        int i = 0;
        for(CommunityPermission permission: communityProjection.getPermissions()){
            userIds[i] = permission.getUserId();
            permissions[i] = permission.getPermission();
            i++;
        }

        jdbcClient
            .sql(sql)
            .param("eventId", eventId)
            .param("communityId", communityProjection.getId())
            .param("isPrivate", communityProjection.isPrivate())
            .param("userIds", userIds)
            .param("permissions", permissions)
            .update();
    }

    @Transactional 
    @Override
    public void update(CommunityProjection communityProjection, UUID eventId) {
        var deleteSql = """
        DELETE FROM community_permission cp
        WHERE cp.community_id = :communityId
        """;

        var updateSql = """
        WITH inserted AS (
            INSERT INTO processed_events(id) VALUES (:eventId)
            ON CONFLICT(id) DO NOTHING
            RETURNING id
        ),
        updated_community AS (
            UPDATE community_projection cp
            SET is_private = :isPrivate
            FROM inserted
            WHERE cp.id = :communityId
            RETURNING cp.*
        )
        INSERT INTO community_permission(community_id, user_id, permission)
        SELECT uc.id, x.user_id, x.permission
        FROM updated_community uc
        CROSS JOIN unnest(
            :userIds::uuid[],
            :permissions::text[]
        ) AS x(user_id, permission)
        """;

        var size = communityProjection.getPermissions().size();
        var userIds = new UUID[size];
        var permissions = new String[size];
        int i = 0;
        for(CommunityPermission permission: communityProjection.getPermissions()){
            userIds[i] = permission.getUserId();
            permissions[i] = permission.getPermission();
            i++;
        }

        jdbcClient
            .sql(deleteSql)
            .param("communityId", communityProjection.getId())
            .update();

        jdbcClient
            .sql(updateSql)
            .param("eventId", eventId)
            .param("communityId", communityProjection.getId())
            .param("isPrivate", communityProjection.isPrivate())
            .param("userIds", userIds)
            .param("permissions", permissions)
            .update();
    }
}
