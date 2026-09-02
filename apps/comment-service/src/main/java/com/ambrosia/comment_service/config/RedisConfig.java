package com.ambrosia.comment_service.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<String, byte[]> byteRedisTemplate(RedisConnectionFactory cf){
        // var valueSerializer = new JacksonJsonRedisSerializer<>(objectMapper, LikeRecord.class);
        var keySerializer = new StringRedisSerializer();
        var redisTemplate = new RedisTemplate<String, byte[]>();
        redisTemplate.setConnectionFactory(cf);
        // redisTemplate.setValueSerializer(valueSerializer);
        // redisTemplate.setHashValueSerializer(valueSerializer);
        redisTemplate.setValueSerializer(RedisSerializer.byteArray());
        redisTemplate.setHashValueSerializer(RedisSerializer.byteArray());
        redisTemplate.setEnableTransactionSupport(false);
        redisTemplate.setKeySerializer(keySerializer);
        redisTemplate.setHashKeySerializer(keySerializer);
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory cf){
        var redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(1))
            .disableCachingNullValues();
        return RedisCacheManager
            .builder(cf)
            .cacheDefaults(redisCacheConfiguration)
            .build();
    }
}
