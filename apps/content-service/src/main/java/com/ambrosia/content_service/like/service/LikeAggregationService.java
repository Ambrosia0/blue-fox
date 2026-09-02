package com.ambrosia.content_service.like.service;

import java.nio.ByteBuffer;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;

import com.ambrosia.content_service.post.repository.PostRepository;

// optimistic like aggregation for posts on redis
public abstract class LikeAggregationService{
    protected final PostRepository postRepository;

    // working with raw bytes for avoiding utf-encoded strings storage
    protected final RedisTemplate<String, byte[]> redisTemplate;
    protected final int setBatchSize = 200;
    protected final int keyBatchSize = 50;

    public LikeAggregationService(
        RedisTemplate<String, byte[]> redisTemplate,
        PostRepository postRepository) {
        this.redisTemplate = redisTemplate;
        this.postRepository = postRepository;
    }
    
    public abstract void add(Long postId, UUID userId, boolean isIncrement);
    public abstract void remove(Long postId, UUID userId, boolean isIncrement);
    public abstract void flush();

    public boolean isCached(long postId, UUID userId, boolean isIncrement){
        return redisTemplate
            .opsForSet()
            .isMember(isIncrement?
                "likes:post:"+postId+":place":
                "likes:post:"+postId+":unplace", userId);
    }

    public boolean[] isPresent(long postId, UUID userId){
        return new boolean[]{
            redisTemplate.opsForSet().isMember("likes:post:"+postId+":place", userId),
            redisTemplate.opsForSet().isMember("likes:post:"+postId+":unplace", userId)
        };
    }

    // uuid to byte array
    protected byte[] uuidToBytes(UUID uuid){
        var buffer = ByteBuffer.allocate(Long.BYTES*2);
        buffer.putLong(uuid.getMostSignificantBits());
        buffer.putLong(uuid.getLeastSignificantBits());
        return buffer.array();
    }
}

