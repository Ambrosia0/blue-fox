package com.ambrosia.content_service.kafka.consumer;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.content_service.follow.repository.CommunityFollowProjectionRepository;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class FollowEventConsumer {
    private final CommunityFollowProjectionRepository communityFollowProjectionRepository;

    @KafkaListener(
        topics = Topics.COMMUNITY_FOLLOW_EVENT,
        batch = "true",
        containerFactory = "kafkaCommunityFollowListenerContainerFactory",
        errorHandler = "serializationErrorHandler"
    )
    public void consume(List<ConsumerRecord<String, byte[]>> message){
        var parsedMessage = message.stream()
            .collect(Collectors.toMap(
                    ConsumerRecord::key, 
                    record -> {
                        try {
                            return CommunityFollowEvent.parseFrom(record.value());
                        } catch (Exception e) {
                            throw new RuntimeException("Invalid body format!");
                        }
                    },
                    (oldVal, newVal) -> newVal
                )
            );
        var batches = parsedMessage.values()
            .stream()
            .collect(Collectors.partitioningBy(CommunityFollowEvent::getFollowed));
        communityFollowProjectionRepository.batchModify(batches.get(true), batches.get(false));
    }
}