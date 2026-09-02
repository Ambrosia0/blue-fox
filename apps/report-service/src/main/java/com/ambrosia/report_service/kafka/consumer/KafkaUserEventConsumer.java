package com.ambrosia.report_service.kafka.consumer;

import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import com.ambrosia.report_service.user.service.UserProjectionService;
import com.google.protobuf.InvalidProtocolBufferException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaUserEventConsumer {
    private final UserProjectionService userProjectionService;

    @KafkaListener(
        topics = Topics.USER_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void on(byte[] message){
        try {
            var parsedMessage = UserEvent.parseFrom(message);
            if(parsedMessage.hasCreated())
                userProjectionService.create(
                    parsedMessage.getCreated(),
                    UUID.fromString(parsedMessage.getEventId())
                );
        } catch (InvalidProtocolBufferException e) {
            log.error("Invalid message format!", e);
            throw new RuntimeException(e);
        }
    }
}
