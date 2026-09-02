package com.ambrosia.content_service.post.repository.custom.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.kafka_events.PostDelta;
import com.ambrosia.content_service.post.repository.custom.CustomPostRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CustomPostRepositoryImpl implements CustomPostRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public int incrementAll(Iterable<Entry<Long, Long>> iterable) {
        var it = iterable.iterator();
        if(!it.hasNext()){
            return 0;
        }
        try(var conn = jdbcTemplate.getDataSource().getConnection()) {
            var statement = conn.prepareStatement("UPDATE post SET like_count= like_count + ? WHERE post.id = ?");
            conn.setAutoCommit(false);
            while (it.hasNext()) {
                var entry = it.next();
                statement.setLong(1, entry.getValue().intValue());
                statement.setLong(2, entry.getKey());
                statement.addBatch();
            }
            var counts = statement.executeBatch();
            conn.commit();
            return Arrays.stream(counts).sum();
        } catch (SQLException e) {
            log.error("Can't increment like count on batch request!", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void batchViewIncrement(Collection<AggregatedViewEvent> toIncrement) {
        var sql = "UPDATE post SET view_count = view_count + ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(AggregatedViewEvent incr: toIncrement){
                stmt.setInt(1, incr.getDelta());
                stmt.setLong(2, incr.getPostId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched view update!", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void batchPreviewIncrement(Collection<AggregatedPreviewEvent> toIncrement) {
        var sql = "UPDATE post SET previewed_count = previewed_count + ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(AggregatedPreviewEvent incr: toIncrement){
                stmt.setInt(1, incr.getDelta());
                stmt.setLong(2, incr.getPostId());
                stmt.addBatch();
            }

            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched preview count update!", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void batchCommentCountIncrement(Collection<PostDelta> toIncrement) {
        var sql = "UPDATE post SET comment_count = comment_count + ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            conn.setAutoCommit(false);
            var stmt = conn.prepareStatement(sql);
            for(PostDelta incr: toIncrement){
                stmt.setInt(1, incr.getDelta());
                stmt.setLong(2, incr.getPostId());
                stmt.addBatch();
            }
            stmt.executeBatch();
            conn.commit();
        } catch (Exception e) {
            log.error("Can't execute batched comment count update!", e);
            throw new RuntimeException(e);
        }
    }
}