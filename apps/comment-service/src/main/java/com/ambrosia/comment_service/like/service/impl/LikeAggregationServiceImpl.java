package com.ambrosia.comment_service.like.service.impl;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.kafka.utils.CommentLikeNotificationFactory;
import com.ambrosia.comment_service.like.model.entity.CommentLike;
import com.ambrosia.comment_service.like.repository.LikeRepository;
import com.ambrosia.comment_service.like.service.LikeAggregationService;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class LikeAggregationServiceImpl extends LikeAggregationService{
    private final LikeRepository likeRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    private RedisScript<Void> addScript = RedisScript.of(
        "redis.call('HSET', KEYS[1], ARGV[1], ARGV[2]); "+
        "redis.call('SADD', KEYS[2], ARGV[3]); "+
        "return 'OK';");

    // atomically (due to one threaded redis) getting all values
    // from hash in format [field value field value ...] and deleting it
    private byte[] fetchScript = 
        ("local data = redis.call('HGETALL', KEYS[1]) "+
        "if #data > 0 then redis.call('DEL', KEYS[1]) end "+
        "return data;").getBytes();
    
    public LikeAggregationServiceImpl(
        LikeRepository likeRepository,
        CommentRepository commentRepository,
        ApplicationEventPublisher applicationEventPublisher,
        RedisTemplate<String, byte[]> redisTemplate){
        super(commentRepository, redisTemplate);
        this.likeRepository = likeRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void add(Long commentId, UUID userId, boolean isIncrement) {
        redisTemplate.execute(
            addScript,
            List.of("comment:"+commentId+":likes", "dirty_comments"),
            uuidToBytes(userId),
            new byte[]{
                (byte)(isIncrement? 1: 0)
            },
            ByteBuffer.allocate(Long.BYTES)
                .putLong(commentId)
                .array()
        );
    }

    @Override
    public void remove(Long commentId, UUID userId, boolean isIncrement) {
        redisTemplate.opsForHash().delete("comment:"+commentId+":likes", uuidToBytes(userId));
    }

    @Override
    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.SECONDS)
    public void flush(){
        try {
            var toIncrement = new HashMap<CommentPostKey, int[]>(200);
            processRedisSet(toIncrement);
            var res = commentRepository.incrementAll(toIncrement
                .entrySet()
                .stream()
                .map(val -> Map.entry(val.getKey().commentId(), val.getValue()[0]))
                .toList());
            applicationEventPublisher.publishEvent(
                CommentLikeNotificationFactory.create(toIncrement)
            );
            if(res != 0)
                log.info("Aggregation executed!");
        } catch (Exception e) {
            log.error("Exception caught while aggregating likes!", e);
        }
    }

    private void processRedisSet(Map<CommentPostKey, int[]> summary){
        var hasMore = false;
        
        try (var conn = redisTemplate.getConnectionFactory().getConnection()) {
            do {
                hasMore = false;

                // getting set of comment ids where likes are updating
                var batch = conn
                    .setCommands()
                    .sPop("dirty_comments".getBytes(), keyBatchSize);
                if(batch.isEmpty())
                    return;

                // if the size of collection with keys equals keyBatchSize, there is more to update
                if(batch.size() == keyBatchSize)
                    hasMore = true;
                conn.openPipeline();

                // executing atomic get&delete from hash for each field
                batch.forEach(key -> conn
                    .scriptingCommands()
                    .eval(fetchScript, ReturnType.MULTI, 1, 
                        ("comment:"+ByteBuffer.wrap(key).getLong()+":likes").getBytes())
                );
                var result = conn.closePipeline();
                var updates = IntStream.range(0, batch.size())
                    .boxed()
                    .flatMap(idx ->{
                        byte[] commentId = batch.get(idx);
                        var byteData = (List<byte[]>)result.get(idx);
                        if(byteData == null || byteData.isEmpty()){
                            return Stream.empty(); // if there is no data by key
                        }
                        return byteData.stream()
                            .gather(Gatherers.windowFixed(2)) // gathering pairs of (field: value) records from redis
                            .map(pair ->{
                                // most significant & least significant bytes for UUID
                                var userIdbuffer = ByteBuffer.wrap(pair.get(0));

                                // getting flag(byte) for increment (like placement)(true) or decrement (like removal) (false)
                                var isIncrement = ByteBuffer.wrap(pair.get(1)).get() > 0;
                                return Map.entry(
                                    CommentLike.create(
                                        ByteBuffer.wrap(commentId).getLong(), 
                                        new UUID(userIdbuffer.getLong(), userIdbuffer.getLong())
                                    ),
                                    isIncrement
                                );
                            });
                        
                    })
                    .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (ex, repl) -> repl
                    ));

                // batch insert/delete, returning query with postId, commentId, number of inserted(+)/deleted(-) records
                // due to optimistic approach, invalid data are ignored
                var saveResult = likeRepository.batchSaveAll(updates.entrySet()
                    .stream()
                    .filter(t -> t.getValue())
                    .map(val -> val.getKey())
                    .toList())
                .stream();
                var deleteResult = likeRepository.batchDeleteAll(updates.entrySet()
                    .stream()
                    .filter(t -> !t.getValue())
                    .map(val -> val.getKey())
                    .toList())
                .stream();

                // combined results going into accumulation map
                Stream.concat(saveResult, deleteResult)
                    .forEach(val -> {summary
                        .computeIfAbsent(CommentPostKey.create(val.postId(), val.commentId()), k -> new int[1])[0]+=val.delta();
                    });

                updates.clear();
            } while (hasMore);  
        }
    }


    public record CommentPostKey(
        Long postId,
        Long commentId
    ){
        public static CommentPostKey create(Long postId, Long commentId){
            return new CommentPostKey(postId, commentId);
        }
    }

}
