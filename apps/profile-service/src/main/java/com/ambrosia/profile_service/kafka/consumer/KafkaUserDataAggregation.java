package com.ambrosia.profile_service.kafka.consumer;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.ambrosia.library_core.dto.Topics;
import com.ambrosia.profile_service.core.UserInfo;
import com.ambrosia.profile_service.kafka_events.UserAggregation;
import com.ambrosia.profile_service.kafka_events.UserInformation;
import com.ambrosia.profile_service.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 *  Consumes Kafka stream events and populates with local user projections
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class KafkaUserDataAggregation{

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    private final UserRepository userRepository;

    @KafkaListener(
        topics = Topics.USER_AGGREGATION,
        groupId = "profile-service",
        errorHandler = "serializationErrorHandler",
        batch = "true",
        containerFactory = "kafkaUserAggregationListenerContainerFactory"
    )
    public void consumeMessage(List<byte[]> message) throws Exception {
        var parsedBatch = message.stream()
            .map(t -> {
                try {
                    return UserAggregation.parseFrom(t);
                } catch (Exception e) {
                    log.error("Unexpected message body!", e);
                    throw new RuntimeException(e);
                }
            })
            .toList();
        var userIds = parsedBatch.stream()
            .map(val ->
                switch (val.getPayloadCase()) {
                    case COMMENT -> UUID.fromString(val.getComment().getUserId());
                    case POST -> UUID.fromString(val.getPost().getAuthorId());
                    case PAYLOAD_NOT_SET -> throw new RuntimeException("Unknown payload!");
                }
            )
            .distinct()
            .toList();
        var users = userRepository.findByIdIn(userIds)
            .stream()
            .collect(Collectors.toMap(key -> key.id().toString(), val -> val));
        
        parsedBatch
            .stream()
            .map(val ->{
                var bodyBuilder = val.toBuilder();

                UserInfo user = switch (val.getPayloadCase()) {
                    case COMMENT -> users.get(bodyBuilder.getComment().getUserId());
                    case POST -> users.get(bodyBuilder.getPost().getAuthorId());
                    case PAYLOAD_NOT_SET -> throw new RuntimeException("Unknown payload!");
                };
                if(user != null){
                    var userBuilder = UserInformation.newBuilder()
                        .setId(user.id().toString())
                        .setUsername(user.username());
                        
                    if(user.avatarId() != null)
                        userBuilder.setAvatarId(user.avatarId());

                    bodyBuilder.setUser(userBuilder.build());
                }
                return bodyBuilder.build();
            })
            .forEach(val -> kafkaTemplate.send(Topics.NOTIFICATION_AGGR, val.toByteArray()));
    }
}
