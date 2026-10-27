package com.ambrosia.report_service.community.repository.custom.impl;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.report_service.community.repository.custom.CustomCommunityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomCommunityRepositoryImpl implements CustomCommunityRepository {
    private final JdbcTemplate jdbcTemplate;
    
    private final String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (?)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO community_projection(id, version)
    SELECT ?, ?
    FROM inserted
    ON CONFLICT(id) DO UPDATE
        SET version = excluded.version
    WHERE community_projection.deleted_at IS NULL
        AND community_projection.version < excluded.version
    """;
    
    private String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES (?)
        ON CONFLICT(id) DO NOTHING
        RETURNING id
    )
    INSERT INTO community_projection AS cp (id, version, deleted_at)
    SELECT ?, ?, CURRENT_TIMESTAMP
    FROM inserted
    ON CONFLICT (id) DO UPDATE
        SET deleted_at = excluded.deleted_at,
            version = excluded.version
    WHERE cp.deleted_at IS NULL
        AND cp.version = excluded.version
    RETURNING cp.id
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
        statement.setLong(3, event.getVersion());
    }
    
    private void setDeleteParams(PreparedStatement statement, CommunityEvent event) throws SQLException{
        statement.setObject(1, UUID.fromString(event.getEventId()));
        statement.setLong(2, event.getId());
        statement.setLong(3, event.getVersion());
    }
}
