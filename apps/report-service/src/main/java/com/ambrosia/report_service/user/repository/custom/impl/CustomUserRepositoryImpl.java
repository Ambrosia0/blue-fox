package com.ambrosia.report_service.user.repository.custom.impl;

import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.report_service.user.repository.custom.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomUserRepositoryImpl implements CustomUserRepository {
    private final JdbcTemplate jdbcTemplate;
    
    private String UPSERT_SQL = """
    WITH inserted AS (
            INSERT INTO processed_events(id) VALUES (?)
                ON CONFLICT (id) DO NOTHING
            RETURNING id
        )
        INSERT INTO user_projection(id, username, avatar_id, version)
        SELECT ?, ?, ?, ?
        FROM inserted
        ON CONFLICT (id)
        DO UPDATE SET
            username = EXCLUDED.username,
            avatar_id = EXCLUDED.avatar_id,
            version = EXCLUDED.version
        WHERE user_projection.deleted_at IS NULL
            AND user_projection.version < EXCLUDED.version
    """;
    
    private String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (?)
            ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO user_projection AS up (id, version, deleted_at)
    SELECT ?, ?, CURRENT_TIMESTAMP
    FROM inserted
        ON CONFLICT(id) DO UPDATE
        SET version = excluded.version,
            username = NULL,
            avatar_id = NULL,
            deleted_at = excluded.deleted_at
    WHERE up.deleted_at IS NULL
        AND up.version = excluded.version
    """;
    
    @Override
    public void process(List<UserEvent> toUpsert, List<UserEvent> toDelete) {
        if(toUpsert.isEmpty() && toDelete.isEmpty())
            return;
        
        try(var conn = jdbcTemplate.getDataSource().getConnection()){
            conn.setAutoCommit(false);
            if(!toUpsert.isEmpty()) {
                var upsertStmt = conn.prepareStatement(UPSERT_SQL);
                for (UserEvent event : toUpsert) {
                    var created = event.getCreated();
                    upsertStmt.setObject(1, UUID.fromString(event.getEventId()));
                    upsertStmt.setObject(2, UUID.fromString(event.getUserId()));
                    upsertStmt.setString(3, created.getUsername());
                    upsertStmt.setString(4, created.getAvatarId());
                    upsertStmt.setLong(5, event.getVersion());
                    upsertStmt.addBatch();
                }
                upsertStmt.executeBatch();
            }
            
            if(!toDelete.isEmpty()) {
                var deleteStmt = conn.prepareStatement(DELETE_SQL);
                for (UserEvent event : toDelete) {
                    deleteStmt.setObject(1, UUID.fromString(event.getEventId()));
                    deleteStmt.setObject(2, UUID.fromString(event.getUserId()));
                    deleteStmt.setLong(3, event.getVersion());
                    deleteStmt.addBatch();
                }
                deleteStmt.executeBatch();
            }
            
            conn.commit();
        } catch (Exception e){
            log.error("Can't process batch of user events!", e);
            throw new RuntimeException(e);
        }
    }
}
