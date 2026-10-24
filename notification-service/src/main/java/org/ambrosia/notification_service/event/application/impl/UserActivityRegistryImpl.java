package org.ambrosia.notification_service.event.application.impl;

import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import org.ambrosia.notification_service.event.application.UserActivityRegistry;
import org.ambrosia.notification_service.kafka_events.ActivityEvent;
import org.ambrosia.notification_service.kafka_events.ActivityEventType;
import org.ambrosia.notification_service.notification.util.ActivityEventFactory;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
public class UserActivityRegistryImpl implements UserActivityRegistry {
    private final static Logger LOG = LoggerFactory.getLogger(UserActivityRegistryImpl.class);

    @Inject
    @Channel("activity-notification")
    Emitter<byte[]> activityEmitter;
    
    @Override
    public Multi<OutboundSseEvent> register(UUID userId) {
        activityEmitter.send(build(userId, ActivityEventType.CONNECT));
        return Multi.createFrom().ticks()
            .every(Duration.ofMinutes(2))
                .invoke(() -> activityEmitter.send(build(userId, ActivityEventType.LEASE_REFRESH)))
            .onTermination()
                .invoke(() -> activityEmitter.send(build(userId, ActivityEventType.DISCONNECT)))
            .onItem()
                .transformToMultiAndMerge(t -> Multi.createFrom().empty());
    }
    
    private Message<byte[]> build(UUID userId, ActivityEventType eventType){
        return Message.of(
            ActivityEventFactory.create(userId, eventType)
                .toByteArray()
        ).addMetadata(OutgoingKafkaRecordMetadata.builder()
            .withKey(userId.toString())
            .build()
        ).withNack(throwable -> {
            LOG.error("[Activity event emitter] Unable to submit {} event!", eventType, throwable);
            return CompletableFuture.completedFuture(null);
        });
    }
}
