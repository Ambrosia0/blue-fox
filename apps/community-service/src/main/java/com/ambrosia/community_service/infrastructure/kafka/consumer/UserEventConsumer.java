package com.ambrosia.community_service.infrastructure.kafka.consumer;

import org.springframework.kafka.annotation.KafkaListener;

import com.ambrosia.community_service.user.service.UserProjectionEventProcessor;
import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.kafka_events.UserEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j 
@RequiredArgsConstructor 
public class UserEventConsumer {
    private final UserProjectionEventProcessor userProjectionEventProcessor;

    @KafkaListener(
        topics = Topics.USER_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void consume(byte[] message){
        try {
            var parsedMessage = UserEvent.parseFrom(message);
            userProjectionEventProcessor.process(parsedMessage);
        } catch (Exception e) {
            log.error("Invalid message format for user!", e);    
        }
    }
}
