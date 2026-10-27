package com.ambrosia.report_service.comment.repository.custom.impl;

import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.report_service.comment.repository.custom.CustomCommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomCommentRepositoryImpl implements CustomCommentRepository {
    private final JdbcTemplate jdbcTemplate;
    
    private final String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES(?)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO comment_projection AS cp (id, version)
    SELECT ?, ?
    FROM inserted
    ON CONFLICT(id) DO UPDATE
        SET version = ?
        WHERE cp.version < excluded.version
            AND cp.deleted_at IS NULL
    """;
    
    private final String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES(?)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO comment_projection AS cp (id, version, deleted_at)
    SELECT ?, ?, CURRENT_TIMESTAMP
    FROM inserted
    ON CONFLICT(id) DO UPDATE
        SET version = excluded.version,
            deleted_at = excluded.deleted_at
        WHERE cp.version = excluded.version
            AND cp.deleted_at IS NULL
    """;
    
    @Override
    public void process(List<CommentEvent> toUpsert, List<CommentEvent> toDelete) {
        try(var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(UPSERT_SQL);
            for(CommentEvent upsert: toUpsert){
                stmt.setObject(1, UUID.fromString(upsert.getEventId()));
                stmt.setLong(2, upsert.getCommentId());
                stmt.setLong(3, upsert.getVersion());
                stmt.setLong(4, upsert.getVersion());
                stmt.addBatch();
            }
            stmt.executeBatch();
            
            stmt = conn.prepareStatement(DELETE_SQL);
            
            for(CommentEvent delete: toDelete){
                stmt.setObject(1, UUID.fromString(delete.getEventId()));
                stmt.setLong(2, delete.getCommentId());
                stmt.setLong(3, delete.getVersion());
                stmt.addBatch();
            }
            stmt.executeBatch();
            
            conn.commit();
        } catch (SQLException ex){
            log.error("Error while processing comment batch!", ex);
            throw new RuntimeException(ex);
        }
    }
}
