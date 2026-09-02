package com.ambrosia.comment_service.comment.repository.custom.impl;

import java.util.Arrays;
import java.util.Map.Entry;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.ambrosia.comment_service.comment.repository.custom.CustomCommentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CustomCommentRepositoryImpl implements CustomCommentRepository{
    private final JdbcTemplate jdbcTemplate;

    @Override
    public long incrementAll(Iterable<Entry<Long, Integer>> iterable) {
        var it = iterable.iterator();
        if(!it.hasNext())
            return 0;
        var sql = "UPDATE comment SET like_count = like_count + ? WHERE id = ?";
        try (var conn = jdbcTemplate.getDataSource().getConnection()) {
            var statement = conn.prepareStatement(sql);
            conn.setAutoCommit(false);
            while (it.hasNext()) {
                var entry = it.next();
                statement.setInt(1, entry.getValue());
                statement.setLong(2, entry.getKey().longValue());
                statement.addBatch();
            }
            var counts = statement.executeBatch();
            conn.commit();
            return Arrays.stream(counts).sum();
        } catch (Exception e) {
            log.error("Can't increment like count on batch request!", e);
            return 0;
        }
    }
}
