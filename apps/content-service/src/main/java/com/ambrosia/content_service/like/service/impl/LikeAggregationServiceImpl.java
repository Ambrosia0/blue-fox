package com.ambrosia.content_service.like.service.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Gatherers;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.ambrosia.content_service.kafka_events.PostLikeNotification;
import com.ambrosia.content_service.like.model.entity.PostLikeKey;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.like.service.LikeAggregationService;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.search.repository.elastic.ElasticPostRepository;

import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LikeAggregationServiceImpl extends LikeAggregationService{
    private final PostLikeRepository postLikeRepository;

    private final @Nullable ElasticPostRepository elasticPostRepository;

    private final ApplicationEventPublisher applicationEventPublisher;

    public LikeAggregationServiceImpl(
        PostLikeRepository postLikeRepository,
        @Nullable ElasticPostRepository elasticPostRepository,
        RedisTemplate<String,byte[]> redisTemplate,
        ApplicationEventPublisher applicationEventPublisher,
        PostRepository postRepository){
        super(redisTemplate, postRepository);
        this.applicationEventPublisher = applicationEventPublisher;
        this.elasticPostRepository = elasticPostRepository;
        this.postLikeRepository = postLikeRepository;
    }

    private final RedisScript<Void> addScript = RedisScript.of(
            "redis.call('HSET', KEYS[1], ARGV[1], ARGV[2]); "+
            "redis.call('SADD', KEYS[2], ARGV[3]); "+
            "return 'OK';");

    private final byte[] fetchScript = 
            ("local data = redis.call('HGETALL', KEYS[1]) "+
            "if #data > 0 then redis.call('DEL', KEYS[1]) end "+
            "return data;").getBytes();

        @Override
    public void add(Long postId, UUID userId, boolean isIncrement) {
        redisTemplate.execute(
            addScript,
            List.of("post:"+postId+":likes", "dirty_posts"),
            uuidToBytes(userId),
            new byte[]{
                (byte)(isIncrement? 1: 0)
            },
            ByteBuffer.allocate(Long.BYTES)
                .putLong(postId)
                .array()
        );
    }

    @Override
    public void remove(Long postId, UUID userId, boolean isIncrement) {
        redisTemplate.opsForHash().delete("post:"+postId+":likes", uuidToBytes(userId));
    }


    @Override
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void flush(){
        try {
            var toIncrement = new ConcurrentHashMap<Long, Long>(200, 0.8f);
            processRedisSet(toIncrement);
            if(elasticPostRepository != null && !toIncrement.isEmpty()){
                elasticPostRepository.incrementLikeCount(toIncrement.entrySet());
            }
            postRepository.incrementAll(toIncrement.entrySet());
            // .create(toIncrement)
            applicationEventPublisher.publishEvent(PostLikeNotification.newBuilder()
                .putAllChanges(toIncrement)
                .build()
            );
        } catch (Exception e) {
            log.error("Caught exception while executing redis like aggregation operation!", e);
        }
    }

    /**
     * Attempt to implement atomic batched like count update through redis
     */
    @SuppressWarnings("unchecked")
    private void processRedisSet(Map<Long, Long> summary){
        var hasMore = false;
        try (var conn = redisTemplate.getConnectionFactory().getConnection()) {
            do {
                hasMore = false;
                var batch = conn
                    .setCommands()
                    .sPop("dirty_posts".getBytes(), keyBatchSize);
                if(batch.isEmpty())
                    return;
                
                // if the size of collection with keys equals keyBatchSize, there is more to update
                if(batch.size() == keyBatchSize)
                    hasMore = true;

                conn.openPipeline();
                batch.forEach(key -> conn
                    .scriptingCommands()
                    .eval(fetchScript, ReturnType.MULTI, 1, 
                        ("post:"+ByteBuffer.wrap(key).getLong()+":likes").getBytes()));
                var result = conn.closePipeline();
                var updates = IntStream.range(0, batch.size())
                    .boxed()
                    .flatMap(idx ->{
                        var byteData = (List<byte[]>)result.get(idx);
                        if(byteData == null || byteData.isEmpty())
                            return Stream.empty();

                        var postId = ByteBuffer.wrap(batch.get(idx)).getLong();
                        return byteData.stream()
                            .gather(Gatherers.windowFixed(2))
                            .map(pair ->{
                                var userIdBuffer = ByteBuffer.wrap(pair.get(0));
                                var isIncrement = ByteBuffer.wrap(pair.get(1)).get() > 0;
                                return Map.entry(
                                    PostLikeKey.create(
                                        new UUID(userIdBuffer.getLong(), userIdBuffer.getLong()),
                                        postId), 
                                    isIncrement
                                );
                            });
                    })
                    .collect(Collectors.toMap(
                        k -> k.getKey(), 
                        v -> v.getValue(),
                        (ex, repl) -> repl
                    ));
                postLikeRepository.batchSaveAll(updates.entrySet()
                    .stream()
                    .filter(t -> t.getValue())
                    .map(val -> val.getKey())
                    .toList())
                    .entrySet()
                    .stream()
                    .forEach(val -> summary.merge(val.getKey(), val.getValue(), Long::sum));

                postLikeRepository.batchDeleteAll(updates.entrySet()
                    .stream()
                    .filter(t -> !t.getValue())
                    .map(val -> val.getKey())
                    .toList())
                    .entrySet()
                    .stream()
                    .forEach(val -> summary.merge(val.getKey(), val.getValue(), Long::sum));
            } while (hasMore);
        }
    }
}