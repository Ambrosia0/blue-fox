package com.ambrosia.profile_service.user.repository.impl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.ambrosia.profile_service.user.model.dto.UserFollowIncrement;
import com.ambrosia.profile_service.user.repository.CustomUserRepository;
import com.ambrosia.profile_service.user.utils.Status;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository{
    private final JdbcTemplate jdbcTemplate;
    private final JdbcClient jdbcClient;

    @Override
    public int batchIncrementFollowCount(Collection<UserFollowIncrement> toIncrement) {
        var sql = """
        UPDATE service_user SET follow_count = follow_count + ? WHERE id = ?        
        """;
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(UserFollowIncrement incr: toIncrement){
                stmt.setInt(1, incr.delta());
                stmt.setObject(2, incr.userId());
                stmt.addBatch();
            }

            var res = stmt.executeBatch();
            conn.commit();
            int sum = 0;
            for(int count: res){
                sum += count;
            }
            return sum;
        } catch (Exception e) {
            log.error("Can't execute batch update of follow count! {}", e);
            return 0;
        }
        
    }

    @Override
    public boolean existsByIds(Collection<UUID> ids) {
        var sql = "SELECT COUNT(id) = :expectedSize FROM service_user WHERE id IN (:ids) AND is_enabled = 'true'";
        return jdbcClient
            .sql(sql)
            .param("ids", ids)
            .param("expectedSize", ids.size())
            .query(Boolean.class)
            .single();
    }
    
    @Override
    public void batchUpdatePresense(List<ActivityEvent> batch) {
        var sql = "UPDATE service_user SET status = ?, last_activity = ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(ActivityEvent a: batch){
                var status = switch(a.getType()){
                    case CONNECT -> Status.ONLINE;
                    case DISCONNECT -> Status.OFFLINE;
                    default -> Status.OFFLINE;
                };
                stmt.setString(1, status.name());
                stmt.setTimestamp(2, Timestamp.from(Instant.ofEpochMilli(a.getTimestamp())));
                stmt.setObject(3, UUID.fromString(a.getUserId()));
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batch update of users presense!", e);
            throw new RuntimeException(e);
        }
    }
}
