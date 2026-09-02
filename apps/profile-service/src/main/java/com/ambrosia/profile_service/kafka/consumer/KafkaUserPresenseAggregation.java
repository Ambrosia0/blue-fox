package com.ambrosia.profile_service.kafka.consumer;

import java.util.List;

import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class KafkaUserPresenseAggregation {
    private final UserRepository userRepository;

    @KafkaListener(
        batch = "true",
        topics = Topics.USER_STATUS,
        containerFactory = "kafkaUserPresenseContainerFactory",
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler"
    )
    void consume(List<byte[]> message){
        IO.println("CONSUME START");
        var toUpdate = message.stream()
            .map(t -> {
                try {
                    return ActivityEvent.parseFrom(t);
                } catch (Exception e) {
                    throw new RuntimeException("Unrecognized message body!");
                }            
            })
            .toList();
        userRepository.batchUpdatePresense(toUpdate);
        IO.println("CONSUME END");
    }
}
