package com.ambrosia.content_service.kafka.consumer;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.ambrosia.content_service.community.repository.CommunityBanProjectionRepository;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CommunityBanEventConsumer {
    private final CommunityBanProjectionRepository communityBanProjectionRepository;

    @KafkaListener(
        topics = Topics.COMMUNITY_BAN_EVENT,
        groupId = "comment-service",
        errorHandler = "serializationErrorHandler",
        batch = "true"
    )
    void consume(List<ConsumerRecord<String, byte[]>> messages){
        var parsedMessage = messages.stream()
            .collect(Collectors.toMap(
                ConsumerRecord::key, 
                record -> {
                    try {
                        return CommunityBanEvent.parseFrom(record.value());
                    } catch (Exception e) {
                        throw new RuntimeException("Invalid body format!");
                    }
                },
                (oldVal, newVal) -> newVal
            ));
        var batches = parsedMessage.values()
            .stream()
            .collect(Collectors.partitioningBy(CommunityBanEvent::hasBan));
        communityBanProjectionRepository.batchModify(batches.get(true), batches.get(false));
    }
    
}
