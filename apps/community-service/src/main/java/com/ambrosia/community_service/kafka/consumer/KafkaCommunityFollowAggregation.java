package com.ambrosia.community_service.kafka.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.community.model.dto.CommunityFollowIncrement;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.kafka_events.CommunityFollowAggregation;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaCommunityFollowAggregation {
    private final CommunityRepository communityRepository;

    @KafkaListener(
        topics = Topics.COMMUNITY_FOLLOW_AGGREGATION,
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler",
        batch = "true",
        containerFactory = "kafkaCommunityFollowListenerContainerFactory"
    )
    void consume(List<byte[]> message) throws Exception{
        var communityFollow = message.stream()
            .map(t -> {
                try {
                    var parsed = CommunityFollowAggregation.parseFrom(t);
                    return new CommunityFollowIncrement(parsed.getCommunityId(), parsed.getDelta());
                } catch (Exception e) {
                    log.error("Unexpected message format! {}", e);
                    throw new RuntimeException(e);
                }
            })
            .toList();
        
        communityRepository.batchIncrementFollowCount(communityFollow);
    }
}
