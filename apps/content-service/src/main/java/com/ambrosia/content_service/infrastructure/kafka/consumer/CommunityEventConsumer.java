package com.ambrosia.content_service.infrastructure.kafka.consumer;

import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class CommunityEventConsumer{

    private final CommunityProjectionRepository communityProjectionRepository;

    @KafkaListener(topics = Topics.COMMUNITY)
    public void processMessage(List<byte[]> messages) {
        var split = messages.stream()
            .map(message -> {
                try {
                    return CommunityEvent.parseFrom(message);
                } catch (InvalidProtocolBufferException ex){
                    log.error("Invalid message body!", ex);
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.partitioningBy(event -> event.hasUpdate() || event.hasCreate()));
        communityProjectionRepository.process(
            split.get(true),
            split.get(false)
        );
    }
}
