package com.ambrosia.comment_service.post.repository.custom;

import java.util.List;

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

    @Override
    public void batchProcess(List<PostEvent> toInsert, List<PostEvent> toDelete) {
        var insertSql = "INSERT INTO post_projection(post_id, community_id) VALUES (?, ?)";
        var deleteSql = "DELETE FROM post_projection WHERE post_id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var insertStmt = conn.prepareStatement(insertSql);
            for(PostEvent insert: toInsert){
                insertStmt.setLong(1, insert.getCreated().getId());
                insertStmt.setLong(2, insert.getCreated().getCommunityId());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            var deleteStmt = conn.prepareStatement(deleteSql);
            for(PostEvent delete: toDelete){
                deleteStmt.setLong(1, delete.getDeleted().getId());
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
