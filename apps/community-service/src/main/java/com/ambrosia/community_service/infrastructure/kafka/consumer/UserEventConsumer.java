package com.ambrosia.community_service.infrastructure.kafka.consumer;

import com.ambrosia.community_service.user.infrastructure.persistence.UserRepository;
import com.google.protobuf.InvalidProtocolBufferException;
import org.springframework.kafka.annotation.KafkaListener;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.kafka_events.UserEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j 
@RequiredArgsConstructor 
public class UserEventConsumer {
    private final UserRepository userRepository;

    @KafkaListener(
        topics = Topics.USER_EVENT,
        errorHandler = "serializationErrorHandler"
    )
    public void consume(List<byte[]> messages){
        try {
            var parsedMessage = messages.stream()
                .map(t -> {
                    try {
                        return UserEvent.parseFrom(t);
                    } catch (InvalidProtocolBufferException e) {
                        log.error("Invalid message body!", e);
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.partitioningBy(event -> event.hasCreated() || event.hasUpdated()));
            userRepository.process(parsedMessage.get(true), parsedMessage.get(false));
        } catch (Exception e) {
            log.error("Invalid message format for user!", e);    
        }
    }
}
