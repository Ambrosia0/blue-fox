package com.ambrosia.profile_service.kafka.consumer;

import java.util.List;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.kafka_events.UserFollowAggregation;
import com.ambrosia.profile_service.user.model.dto.UserFollowIncrement;
import com.ambrosia.profile_service.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaUserFollowAggregation {
    
    private final UserRepository userRepository;

    @KafkaListener(
        topics = Topics.USER_FOLLOW_AGGREGATION,
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler",
        batch = "true",
        containerFactory = "kafkaUserFollowListenerContainerFactory")
    public void consume(List<byte[]> message) throws Exception{
        var userFollow = message.stream()
            .map(t -> {
                try {
                    var parsed = UserFollowAggregation.parseFrom(t);
                    return new UserFollowIncrement(
                        UUID.fromString(parsed.getUserId()), 
                        parsed.getDelta()
                    );
                } catch (Exception e) {
                    log.error("Unexpected message format!", e);
                    throw new RuntimeException(e);
                }
            })
            .toList();
        userRepository.batchIncrementFollowCount(userFollow);
    }
}
