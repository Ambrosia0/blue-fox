package com.ambrosia.comment_service.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.community.service.CommunityProjectionService;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RequiredArgsConstructor
@Component
public class KafkaCommunityEventConsumer {
    private final CommunityProjectionService communityProjectionService;

    @KafkaListener(
        topics = Topics.COMMUNITY,
        groupId = "comment-service",
        errorHandler = "serializationErrorHandler"
    )
    void consume(byte[] message){
        try {
            var communityEvent = CommunityEvent.parseFrom(message);
            communityProjectionService.process(communityEvent);
        } catch (Exception e) {
            log.error("Invalid message body!", e);
            throw new RuntimeException(e);
        }
    }
}
