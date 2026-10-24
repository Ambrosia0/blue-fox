package com.ambrosia.content_service.user.infrastructure.persistence.impl;

import com.ambrosia.content_service.user.infrastructure.persistence.CustomUserRepository;
import com.ambrosia.content_service.user.infrastructure.persistence.UserRepository;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class JdbcUserRepository implements CustomUserRepository {
    private final JdbcTemplate jdbcTemplate;
    
    private String UPSERT_SQL = """
    WITH inserted AS (
            INSERT INTO processed_events(id)
            VALUES (?)
            RETURNING id
        )
        INSERT INTO user_projection(id, username, first_name, last_name, avatar_id, version)
        SELECT ?, ?, ?, ?, ?, ?
        FROM inserted
        ON CONFLICT (id)
        DO UPDATE SET
            username = EXCLUDED.username,
            first_name = EXCLUDED.first_name,
            last_name = EXCLUDED.last_name,
            avatar_id = EXCLUDED.avatar_id,
            version = EXCLUDED.version
        WHERE user_projection.version < EXCLUDED.version
    """;
    
    private String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id)
        VALUES (?)
        RETURNING id
    )
    DELETE FROM user_projection
    WHERE id = ?
    AND version = ?
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
                    upsertStmt.setString(4, created.getFirstName());
                    upsertStmt.setString(5, created.getLastName());
                    upsertStmt.setString(6, created.getAvatarId());
                    upsertStmt.setLong(7, event.getVersion());
                }
                upsertStmt.executeBatch();
            }
            
            if(!toDelete.isEmpty()) {
                var deleteStmt = conn.prepareStatement(DELETE_SQL);
                for (UserEvent event : toDelete) {
                    deleteStmt.setObject(1, UUID.fromString(event.getEventId()));
                    deleteStmt.setObject(2, UUID.fromString(event.getUserId()));
                    deleteStmt.setLong(3, event.getVersion());
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
