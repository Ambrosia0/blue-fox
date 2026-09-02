package com.ambrosia.comment_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.community.service.CommunityProjectionService;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaCommunityEventConsumer {
    private final CommunityProjectionService communityProjectionService;

    @KafkaListener(
        topics = Topics.COMMUNITY,
        groupId = "comment-service",
        errorHandler = "serializationErrorHandler"
    )
    void consume(byte[] message) throws Exception{
        var communityEvent = CommunityEvent.parseFrom(message);
        switch (communityEvent.getEventCase()) {
            case CREATE -> communityProjectionService.create(communityEvent);
            case DELETE -> communityProjectionService.delete(communityEvent);
            default ->{}
        }
    }
}
