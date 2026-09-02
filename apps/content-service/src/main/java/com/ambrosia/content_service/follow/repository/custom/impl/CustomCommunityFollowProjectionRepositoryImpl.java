package com.ambrosia.content_service.follow.repository.custom.impl;

import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.content_service.follow.repository.custom.CustomCommunityFollowProjectionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomCommunityFollowProjectionRepositoryImpl implements CustomCommunityFollowProjectionRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void batchModify(Collection<CommunityFollowEvent> toInsert, Collection<CommunityFollowEvent> toDelete) {
        var insertSql = "INSERT INTO community_follow_projection(user_id, community_id) VALUES (?, ?) ON CONFLICT (user_id, community_id) DO NOTHING";
        var deleteSql = "DELETE FROM community_follow_projection WHERE user_id = ? AND community_id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            try (var insertStmt = conn.prepareStatement(insertSql)) {
                for(CommunityFollowEvent followed: toInsert){
                    insertStmt.setString(1, followed.getRequestingUser());
                    insertStmt.setLong(2, followed.getCommunityId());
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
            }

            try (var deleteStmt = conn.prepareStatement(deleteSql)){
                for(CommunityFollowEvent unfollow: toDelete){
                    deleteStmt.setString(1, unfollow.getRequestingUser());
                    deleteStmt.setLong(2, unfollow.getCommunityId());
                    deleteStmt.addBatch();
                }
                deleteStmt.executeBatch();
            }
            conn.commit();
        } catch (Exception e) {
            log.error("Can't batch modify community follow projections!", e);
            throw new RuntimeException(e);
        }
    }
}
