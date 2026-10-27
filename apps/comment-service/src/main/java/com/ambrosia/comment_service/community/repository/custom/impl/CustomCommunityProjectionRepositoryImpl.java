package com.ambrosia.comment_service.community.repository.custom.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.community_service.kafka_events.ModeratorPermissions;
import com.ambrosia.community_service.kafka_events.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.comment_service.community.model.entity.CommunityPermission;
import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.custom.CustomCommunityProjectionRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@RequiredArgsConstructor 
@Repository 
public class CustomCommunityProjectionRepositoryImpl implements CustomCommunityProjectionRepository{
    private final JdbcTemplate jdbcTemplate;
    
    private final String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (?)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    ),
    inserted_community AS (
        INSERT INTO community_projection(id, is_private, version)
        SELECT ?, ?, ?
        FROM inserted
        ON CONFLICT(id) DO UPDATE
            SET is_private = excluded.is_private,
                version = excluded.version
        WHERE community_projection.deleted_at IS NULL
            AND community_projection.version < excluded.version
        RETURNING *
    ),
    deleted_permissions AS (
        DELETE FROM community_permission cp
            USING inserted_community ic
            WHERE cp.community_id = ic.id
            RETURNING cp.community_id
    )
    INSERT INTO community_permission(community_id, user_id, permission)
    SELECT ic.id, x.user_id, x.permission
    FROM inserted_community ic
    CROSS JOIN unnest(?::uuid[], ?::text[]) AS x(user_id, permission)
    CROSS JOIN (
        SELECT COUNT(*) as deleted_count
        FROM deleted_permissions
    ) dp
    """;
    
    private String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (?)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    ),
    deleted_community AS (
        INSERT INTO community_projection AS cp (id, version, deleted_at)
        SELECT ?, ?, CURRENT_TIMESTAMP
        FROM inserted
        ON CONFLICT (id) DO UPDATE
            SET is_private = NULL,
                deleted_at = excluded.deleted_at,
                version = excluded.version
        WHERE cp.deleted_at IS NULL
            AND cp.version = excluded.version
        RETURNING cp.id
    ),
    deleted_permissions AS (
        DELETE FROM community_permission
            WHERE community_id = ?
    )
    DELETE FROM community_follow_projection
    WHERE community_id = ?
    """;
    
    @Override
    public void process(List<CommunityEvent> toUpsert, List<CommunityEvent> toDelete) {
        try(var conn = jdbcTemplate.getDataSource().getConnection()){
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(UPSERT_SQL);
            for(CommunityEvent upsert: toUpsert){
                setUpsertParams(stmt, upsert);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            
            stmt = conn.prepareStatement(DELETE_SQL);
            for(CommunityEvent delete: toDelete){
                setDeleteParams(stmt, delete);
                stmt.addBatch();
            }
            
            stmt.executeBatch();
            conn.commit();
        } catch (SQLException ex){
            log.error("Can't process community batch!", ex);
            throw new RuntimeException(ex);
        }
    }
    
    private void setUpsertParams(PreparedStatement statement, CommunityEvent event) throws SQLException {
        statement.setObject(1, UUID.fromString(event.getEventId()));
        statement.setLong(2, event.getId());
        statement.setLong(4, event.getVersion()); //version
        
        var permissionList = switch (event.getEventCase()){
            case CREATE -> {
                statement.setBoolean(3, event.getCreate().getIsPrivate());
                yield getPermissions(event.getCreate().getCommunityPermissionsMap());
            }
            case UPDATE -> {
                statement.setBoolean(3, event.getUpdate().getIsPrivate());
                yield getPermissions(event.getUpdate().getCommunityPermissionsMap());
            }
            default -> throw new IllegalArgumentException("Invalid message body!");
        };
        
        UUID[] userIds = new UUID[permissionList.size()];
        String[] permissions = new String[permissionList.size()];
        int i = 0;
        for(CommunityPermission p: permissionList){
            userIds[i] = p.getUserId();
            permissions[i] = p.getPermission();
            i++;
        }
        
        statement.setArray(5, statement.getConnection().createArrayOf("uuid", userIds));
        statement.setArray(6, statement.getConnection().createArrayOf("text", permissions));
    }
    
    private void setDeleteParams(PreparedStatement statement, CommunityEvent event) throws SQLException{
        statement.setObject(1, UUID.fromString(event.getEventId()));
        statement.setLong(2, event.getId());
        statement.setLong(3, event.getVersion());
        statement.setLong(4, event.getId());
        statement.setLong(5, event.getId());
    }
    
    private List<CommunityPermission> getPermissions(Map<String, ModeratorPermissions> permissions){
        return permissions.entrySet()
            .stream()
            .map(pair -> pair.getValue().getPermissionsList().stream()
                .filter(p -> Permission.valueOf(p.name()) == Permission.COMMENT_DELETE)
                .findFirst()
                .map(t -> new CommunityPermission(t.name(), UUID.fromString(pair.getKey())))
                .orElse(new CommunityPermission("", UUID.fromString(pair.getKey())))
            )
            .toList();
    }
}
