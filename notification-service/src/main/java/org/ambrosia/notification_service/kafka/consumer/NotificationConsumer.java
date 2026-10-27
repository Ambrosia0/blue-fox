package org.ambrosia.notification_service.kafka.consumer;

import com.google.protobuf.InvalidProtocolBufferException;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.tuples.Tuple2;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.event.application.UserEventDispatcher;
import org.ambrosia.notification_service.event.application.UserEventRegistry;
import org.ambrosia.notification_service.notification.infrastructure.persistence.CommunityNotificationRepository;
import org.ambrosia.notification_service.notification.infrastructure.persistence.TargetedNotificationRepository;
import org.ambrosia.notification_service.notification.infrastructure.persistence.UserNotificationRepository;
import org.ambrosia.notification_service.notification.infrastructure.persistence.impl.VertxTargetedNotificationRepository;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@ApplicationScoped
public class NotificationConsumer {
    private static Logger LOG = LoggerFactory.getLogger(NotificationConsumer.class);
    
    private final CommunityNotificationRepository communityNotificationRepository;
    
    private final UserNotificationRepository userNotificationRepository;
    
    private final TargetedNotificationRepository targetedNotificationRepository;
    
    public NotificationConsumer(
            CommunityNotificationRepository communityNotificationRepository,
            UserNotificationRepository userNotificationRepository,
            TargetedNotificationRepository targetedNotificationRepository
    ) {
        this.communityNotificationRepository = communityNotificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.targetedNotificationRepository = targetedNotificationRepository;
    }
    
    @Incoming("notification-persistence")
    public Uni<Void> consume(ConsumerRecords<String, byte[]> messages){
        var parsed = StreamSupport.stream(messages.spliterator(), false)
            .<Notification>mapMulti((message, consumer) -> {
                try {
                    consumer.accept(Notification.parseFrom(message.value()));
                } catch (InvalidProtocolBufferException e) {
                    LOG.error("[Notification Batch] Invalid message body!");
                }
            })
            .collect(Collectors.groupingBy(Notification::getPayloadCase, Collectors.toList()));
        return communityNotificationRepository
            .process(parsed.get(Notification.PayloadCase.COMMUNITY_NOTIFICATION))
            .chain(() -> userNotificationRepository.process(parsed.get(Notification.PayloadCase.USER_NOTIFICATION)))
            .chain(() -> targetedNotificationRepository.process(parsed.get(Notification.PayloadCase.TARGETED_NOTIFICATION)))
            .replaceWithVoid();
    }
}
