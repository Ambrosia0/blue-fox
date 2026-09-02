package com.ambrosia.comment_service.community.repository.impl;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.community.repository.CustomCommunityBanRepository;
import com.ambrosia.community_service.kafka_events.CommunityBanEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomCommunityBanRepositoryImpl implements CustomCommunityBanRepository{
    private final JdbcTemplate jdbcTemplate;
    
    @Override
    public void batchModify(List<CommunityBanEvent> toInsert, List<CommunityBanEvent> toDelete) {
        var insertSql = "INSERT INTO community_ban_projection(community_id, user_id) VALUES (?, ?)";
        var deleteSql = "DELETE FROM community_ban_projection WHERE community_id = ? AND user_id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var insertStmt = conn.prepareStatement(insertSql);
            for(CommunityBanEvent insert: toInsert){
                insertStmt.setLong(1, insert.getBan().getCommunityId());
                insertStmt.setObject(2, insert.getBan().getUserId());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            var deleteStmt = conn.prepareStatement(deleteSql);
            for(CommunityBanEvent delete: toDelete){
                deleteStmt.setLong(1, delete.getUnban().getCommunityId());
                deleteStmt.setObject(2, delete.getUnban().getUserId());
                deleteStmt.addBatch();
            }
            deleteStmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched operations on community ban projections!", e);
            throw new RuntimeException(e);
        }
        
    }
}
