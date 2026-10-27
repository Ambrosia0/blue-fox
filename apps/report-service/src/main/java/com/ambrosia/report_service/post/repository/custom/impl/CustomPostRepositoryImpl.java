package com.ambrosia.report_service.post.repository.custom.impl;

import com.ambrosia.content_service.kafka_events.PostEvent;
import com.ambrosia.report_service.post.repository.custom.CustomPostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomPostRepositoryImpl implements CustomPostRepository {
    private final JdbcTemplate jdbcTemplate;
    
    private final String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES(?)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO post_projection AS pp (id, is_published, version)
    SELECT ?, ?, ?
    FROM inserted
    ON CONFLICT(id) DO UPDATE
        SET is_published = excluded.is_published,
            version = excluded.version
    WHERE pp.deleted_at IS NULL
        AND pp.version < excluded.version
    RETURNING *
    """;
    
    private final String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES(?)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO post_projection AS pp (id, version, deleted_at)
        SELECT ?, ?, CURRENT_TIMESTAMP
        FROM inserted
        ON CONFLICT (id) DO UPDATE
            SET is_published = NULL,
                deleted_at = excluded.deleted_at,
                version = excluded.version
            WHERE pp.version = excluded.version
                AND pp.deleted_at IS NULL
    """;
    
    
    @Override
    public void process(List<PostEvent> toUpsert, List<PostEvent> toDelete) {
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var insertStmt = conn.prepareStatement(UPSERT_SQL);
            for(PostEvent insert: toUpsert){
                insertStmt.setObject(1, UUID.fromString(insert.getEventId()));
                insertStmt.setLong(2, insert.getPostId());
                switch (insert.getEventCase()) {
                    case CREATED ->{
                        insertStmt.setBoolean(3, true);
                        insertStmt.setLong(4, insert.getVersion());
                    }
                    case UPDATED ->{
                        insertStmt.setBoolean(3, insert.getUpdated().getIsPublished());
                        insertStmt.setLong(4, insert.getVersion());
                    }
                }
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
            
            var deleteStmt = conn.prepareStatement(DELETE_SQL);
            for(PostEvent delete: toDelete){
                deleteStmt.setObject(1, UUID.fromString(delete.getEventId()));
                deleteStmt.setLong(2, delete.getPostId());
                deleteStmt.setLong(3, delete.getVersion());
                deleteStmt.addBatch();
            }
            deleteStmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batch operations on post projections!", e);
            throw new RuntimeException(e);
        }
    }
}
