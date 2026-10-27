package com.ambrosia.comment_service.post.repository.custom;

import java.util.List;
import java.util.UUID;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.post.repository.CustomPostProjectionRepository;
import com.ambrosia.content_service.kafka_events.PostEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomPostProjectionRepositoryImpl implements CustomPostProjectionRepository{
    private final JdbcTemplate jdbcTemplate;
    
    private final String UPSERT_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES(?)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO post_projection(post_id, community_id, is_published, version)
    SELECT ?, ?, ?, ?
    FROM inserted
    ON CONFLICT(post_id) DO UPDATE
        SET community_id = excluded.community_id,
            is_published = excluded.is_published,
            version = excluded.version
    WHERE post_projection.deleted_at IS NULL
        AND post_projection.version < excluded.version
    RETURNING *
    """;
    
    private final String DELETE_SQL = """
    WITH inserted AS (
        INSERT INTO processed_events(id) VALUES(?)
        ON CONFLICT (id) DO NOTHING
        RETURNING id
    )
    INSERT INTO post_projection AS pp (post_id, version, deleted_at)
        SELECT ?, ?, CURRENT_TIMESTAMP
        FROM inserted
        ON CONFLICT (post_id) DO UPDATE
            SET community_id = NULL,
                is_published = NULL,
                deleted_at = excluded.deleted_at,
                version = excluded.version
            WHERE pp.version = excluded.version
                AND pp.deleted_at IS NULL
    """;

    @Override
    public void process(List<PostEvent> toInsert, List<PostEvent> toDelete) {
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var insertStmt = conn.prepareStatement(UPSERT_SQL);
            for(PostEvent insert: toInsert){
                insertStmt.setObject(1, UUID.fromString(insert.getEventId()));
                insertStmt.setLong(2, insert.getPostId());
                switch (insert.getEventCase()) {
                    case CREATED ->{
                        insertStmt.setLong(3, insert.getCreated().getCommunity().getId());
                        insertStmt.setBoolean(4, true);
                        insertStmt.setLong(5, insert.getVersion());
                    }
                    case UPDATED ->{
                        insertStmt.setLong(3, insert.getUpdated().getCommunityId());
                        insertStmt.setBoolean(4, insert.getUpdated().getIsPublished());
                        insertStmt.setLong(5, insert.getVersion());
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
