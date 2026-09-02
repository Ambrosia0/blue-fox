package com.ambrosia.report_service.kafka.consumer;


import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.report_service.community.service.CommunityProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaCommunityEventConsumer {
    private final CommunityProjectionService communityProjectionService;

    @KafkaListener(
        topics = Topics.COMMUNITY,
        errorHandler = "serializationErrorHandler"
    )
    public void on(byte[] message){
        try {
            var parsedMessage = CommunityEvent.parseFrom(message);
            if(parsedMessage.hasCreate())
                communityProjectionService.create(
                    parsedMessage.getCreate(),
                    UUID.fromString(parsedMessage.getEventId())
                );
            else if(parsedMessage.hasDelete())
                communityProjectionService.delete(
                    parsedMessage.getDelete(),
                    UUID.fromString(parsedMessage.getEventId())
                );
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid message format!", e);
            throw new RuntimeException(e);
        }
    }
}
