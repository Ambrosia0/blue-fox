package com.ambrosia.community_service.infrastructure.kafka.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.infrastructure.persistence.JdbcCommunityRepository;
import com.ambrosia.community_service.kafka_events.PostCountAggregation;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaCommunityPostCountAggregation {
    private final JdbcCommunityRepository communityRepository;
    
    @KafkaListener(
        topics = Topics.COMMUNITY_POST_COUNT_AGGREGATION,
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler",
        batch = "true",
        containerFactory = "kafkaCommunityPostCountListenerContainerFactory"
    )
    void consume(List<byte[]> message){
        var parsedMessage = message.stream()
            .map(t -> {
                try {
                    return PostCountAggregation.parseFrom(t);
                } catch (Exception e) {
                    throw new RuntimeException("Invalid body!", e);
                }
            })
            .toList();
        communityRepository.batchIncrementPostCount(parsedMessage);
    }
}
