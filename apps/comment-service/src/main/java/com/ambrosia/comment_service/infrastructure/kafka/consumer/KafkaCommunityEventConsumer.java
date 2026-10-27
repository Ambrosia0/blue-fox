package com.ambrosia.comment_service.infrastructure.kafka.consumer;

import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.library_core.dto.Topics;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j 
@RequiredArgsConstructor
@Component
public class KafkaCommunityEventConsumer {
    private final CommunityProjectionRepository communityProjectionRepository;

    @KafkaListener(
        topics = Topics.COMMUNITY,
        groupId = "comment-service",
        errorHandler = "serializationErrorHandler"
    )
    void consume(List<byte[]> messages){
        var parsed = messages.stream()
            .map(message -> {
                try {
                    return CommunityEvent.parseFrom(message);
                } catch (InvalidProtocolBufferException e){
                    log.error("Invalid message payload!", e);
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.partitioningBy(m -> m.hasCreate() || m.hasUpdate()));
        communityProjectionRepository.process(
            parsed.get(true),
            parsed.get(false)
        );
    }
}
