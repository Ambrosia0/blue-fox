package org.ambrosia.notification_service.kafka.consumer;

import com.ambrosia.content_service.kafka_events.UserFollowEvent;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.UserFollowKey;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxUserFollowRepository;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.google.protobuf.InvalidProtocolBufferException;

import jakarta.enterprise.context.ApplicationScoped;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped 
public class UserFollowConsumer {
    private final Logger LOG = LoggerFactory.getLogger(UserFollowConsumer.class);

    @Inject
    VertxUserFollowRepository vertxUserFollowRepository;

    @Incoming("user-follow")
    public Uni<Void> consume(ConsumerRecords<String, byte[]> messages) {
        var parsed = StreamSupport.stream(messages.spliterator(), false)
            .map(message -> {
                try{
                    return UserFollowEvent.parseFrom(message.value());
                } catch (InvalidProtocolBufferException e){
                    LOG.error("[User Follow Batch] Invalid body format!", e);
                    throw new RuntimeException(e);
                }
            })
            .collect(Collectors.toMap(k -> switch (k.getEventCase()){
                        case FOLLOWED -> {
                            var event = k.getFollowed();
                            yield new UserFollowKey(
                                UUID.fromString(event.getRequestingUser()),
                                UUID.fromString(event.getFollowedUserId())
                            );
                        }
                        case UNFOLLOWED -> {
                            var event = k.getUnfollowed();
                            yield new UserFollowKey(
                                UUID.fromString(event.getRequestingUser()),
                                UUID.fromString(event.getFollowedUserId())
                            );
                        }
                        default -> {
                            throw new IllegalArgumentException("Unknown body!");
                        }
                    },
                    UserFollowEvent::getEventCase,
                    (oldVal,newVal) ->
                        switch (newVal) {
                            case FOLLOWED -> oldVal == UserFollowEvent.EventCase.UNFOLLOWED?
                                null:
                                newVal;
                            case UNFOLLOWED -> oldVal == UserFollowEvent.EventCase.FOLLOWED?
                                null:
                                newVal;
                            default -> {
                                throw new RuntimeException();
                            }
                        }

                )
            )
            .entrySet()
            .stream()
            .collect(Collectors.partitioningBy(
                    entry -> entry.getValue() == UserFollowEvent.EventCase.FOLLOWED,
                    Collectors.mapping(Map.Entry::getKey, Collectors.toList())
                )
            );
        return vertxUserFollowRepository.applyChanges(
            parsed.get(true),
            parsed.get(false)
        );
    }
}
