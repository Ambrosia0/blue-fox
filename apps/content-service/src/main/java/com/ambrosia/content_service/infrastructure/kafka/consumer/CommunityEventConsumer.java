package com.ambrosia.content_service.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.content_service.community.service.CommunityProjectionEventProcessor;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
@Component
public class CommunityEventConsumer{

    private final CommunityProjectionEventProcessor communityProjectionService;

    @SneakyThrows
    @KafkaListener(topics = Topics.COMMUNITY)
    public void processMessage(byte[] message) {
        var communityEvent = CommunityEvent.parseFrom(message);
        communityProjectionService.process(communityEvent);
    }
}
