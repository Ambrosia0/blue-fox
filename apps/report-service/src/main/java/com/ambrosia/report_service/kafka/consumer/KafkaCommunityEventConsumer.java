package com.ambrosia.report_service.kafka.consumer;

import com.ambrosia.report_service.community.repository.CommunityProjectionRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.report_service.community.service.CommunityProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

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
        errorHandler = "serializationErrorHandler"
    )
    public void on(List<byte[]> messages){
        var parsed = messages.stream()
            .map(m -> {
                try {
                    return CommunityEvent.parseFrom(m);
                } catch (InvalidProtocolBufferException ex){
                    log.error("Invalid message body in community event!");
                    throw new RuntimeException(ex);
                }
            })
            .collect(Collectors.partitioningBy(e -> e.hasUpdate() || e.hasCreate()));
        communityProjectionRepository.process(parsed.get(true), parsed.get(false));
    }
}
