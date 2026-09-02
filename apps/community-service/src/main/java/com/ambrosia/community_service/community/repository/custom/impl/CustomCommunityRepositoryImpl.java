package com.ambrosia.community_service.community.repository.custom.impl;

import java.util.Collection;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.community_service.community.model.dto.CommunityFollowIncrement;
import com.ambrosia.community_service.community.repository.custom.CustomCommunityRepository;
import com.ambrosia.community_service.kafka_events.PostCountAggregation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@RequiredArgsConstructor
public class CustomCommunityRepositoryImpl implements CustomCommunityRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int batchIncrementFollowCount(Collection<CommunityFollowIncrement> toIncrement) {
        var sql = "UPDATE community SET follow_count = follow_count + ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(CommunityFollowIncrement incr: toIncrement){
                stmt.setInt(1, incr.delta());
                stmt.setLong(2, incr.communityId());
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
            log.error("Can't execute batch update on community follow counter! {}", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public int batchIncrementPostCount(Collection<PostCountAggregation> toIncrement) {
        var sql = "UPDATE community SET post_count = post_count + ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(PostCountAggregation incr: toIncrement){
                stmt.setInt(1, incr.getDelta());
                stmt.setLong(2, incr.getCommunityId());
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
            log.error("Can't execute batch update on community post count! {}", e);
            throw new RuntimeException(e);
        }
    }
}
