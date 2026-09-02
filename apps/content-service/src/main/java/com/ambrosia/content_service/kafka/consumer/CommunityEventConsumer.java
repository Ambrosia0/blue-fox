package com.ambrosia.content_service.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.content_service.community.service.CommunityProjectionService;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Component
public class CommunityEventConsumer{

    private final CommunityProjectionService communityProjectionService;

    @SneakyThrows
    @KafkaListener(topics = Topics.COMMUNITY)
    public void processMessage(byte[] message) {
        var communityEvent = CommunityEvent.parseFrom(message);
        switch (communityEvent.getEventCase()) {
            case CREATE -> communityProjectionService.create(communityEvent);
            case DELETE -> communityProjectionService.delete(communityEvent);
            case UPDATE -> communityProjectionService.update(communityEvent);
            default -> throw new RuntimeException("Unrecognized message content!");
        }
    }
}
