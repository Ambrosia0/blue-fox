package com.ambrosia.comment_service.like.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.jspecify.annotations.Nullable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.DefaultStringRedisConnection;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands.SetOption;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;

import com.ambrosia.comment_service.comment.model.dto.PostCommentTuple;
import com.ambrosia.comment_service.like.service.CacheService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommentNumberCache implements CacheService<List<Long>, List<PostCommentTuple>>{
    private final RedisTemplate<String, Long> redisTemplate;

    @Override
    public void push(List<PostCommentTuple> t, long ttl, TimeUnit timeUnit) {
        redisTemplate.executePipelined(new RedisCallback<Object>() {
            @Override
            public @Nullable Object doInRedis(RedisConnection connection) throws DataAccessException {
                @SuppressWarnings("resource")
                var stringRedisConn = new DefaultStringRedisConnection(connection);
                t.forEach(v -> stringRedisConn.set(
                    Long.toString(v.id()), 
                    Long.toString(v.commentCount()), 
                    Expiration.from(ttl, timeUnit), 
                    SetOption.UPSERT));
                return null;
            }
        });
    }

    @Override
    public List<PostCommentTuple> get(List<Long> t) {
        var res =  redisTemplate.opsForValue().multiGet(t.stream().map(Object::toString).toList());
        var stream = IntStream.range(0, res.size());
        return stream
            .boxed()
            .filter(idx -> res.get(idx) != null)
            .map(idx -> new PostCommentTuple(t.get(idx), res.get(idx).intValue()))
            .toList();
    }
}
