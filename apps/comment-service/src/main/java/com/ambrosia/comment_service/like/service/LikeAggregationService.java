package com.ambrosia.comment_service.like.service;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;

import com.ambrosia.comment_service.comment.repository.CommentRepository;

import lombok.RequiredArgsConstructor;


// optimistic like aggregation for comments on redis
@RequiredArgsConstructor
public abstract class LikeAggregationService{
    protected final CommentRepository commentRepository;

    // using raw bytes for avoiding utf-encoded strings storaging
    protected final RedisTemplate<String, byte[]> redisTemplate;

    // dafuq
    protected int batchSize = 200;

    // limit for maximum keys processed per aggregation iteration
    protected int keyBatchSize = 100;

    public abstract void add(Long commentId, UUID userId, boolean isIncrement);
    public abstract void remove(Long commentId, UUID userId, boolean isIncrement);
    public abstract void flush();
    
    // check if user is present in queue
    public boolean isCached(Long commentId, UUID userId){
        return redisTemplate.opsForHash().hasKey("comment:"+commentId+":likes", userId);
    }

    // converting uuid to byte array
    protected byte[] uuidToBytes(UUID uuid){
        var buffer = ByteBuffer.allocate(Long.BYTES*2);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }

}
