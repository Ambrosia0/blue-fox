package org.ambrosia.notification_service.notification.application;

import static io.restassured.RestAssured.given;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.ambrosia.notification_service.kafka_events.TargetedNotification;
import org.ambrosia.notification_service.notification.application.query.NotificationQueryService;
import org.ambrosia.notification_service.projections.infrastructure.persistence.UserRepository;
import org.ambrosia.notification_service.utils.NotificationFilterBuilder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class NotificationQueryServiceTest {
    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;
    
    @Inject
    UserRepository userRepository;

    @Inject
    NotificationQueryService notificationQueryService;
    
    @Test
    void shouldReturnUserNotification(){
        var userId = UUID.randomUUID();
        createUser(userId);
        var message = sendNotification(userId);
        
        await(
            () -> Assertions.assertTrue(
                () -> notificationQueryService.getNotifications(
                        NotificationFilterBuilder.builder().build(),
                        Actor.from(userId, Actor.Role.USER)
                    ).await()
                    .atMost(Duration.ofSeconds(1))
                    .stream()
                    .anyMatch(resp -> resp
                        .content()
                        .equals(message
                            .getTargetedNotification()
                            .getContent()
                        )
                    )
            )
        );
    }
    
    @Test
    void shouldSendNewNotificationsCount(){
        var id = UUID.randomUUID();
        createUser(id);
        
        sendNotification(id);
        
        await(
            () -> Assertions.assertTrue(
                () -> notificationQueryService.getNewNotificationsCount(Actor.from(id, Actor.Role.USER))
                    .await()
                    .atMost(Duration.ofSeconds(1)) > 0
            )
        );
    }
    
    private Notification sendNotification(UUID id){
        var message = createTargetedNotification(id);
        kafkaCompanion.produce(String.class, byte[].class)
            .fromRecords(new ProducerRecord<String, byte[]>(
                "blog.notification",
                0,
                Instant.now().toEpochMilli(),
                id.toString(),
                message.toByteArray()
            ))
            .awaitCompletion();
        return message;
    }
    
    private Notification createTargetedNotification(UUID id){
        return Notification.newBuilder()
            .setTargetedNotification(TargetedNotification.newBuilder()
                .setUserId(id.toString())
                .setContent("test_content")
                .build()
            )
            .build();
    }
    
    private void await(ThrowingRunnable runnable){
        Awaitility.await().atMost(Duration.ofSeconds(8))
            .pollInterval(Duration.ofSeconds(1))
            .untilAsserted(runnable);
    }
    
    private void createUser(UUID id){
        userRepository.process(
            List.of(UserEvent.newBuilder()
                .setUserId(id.toString())
                .setEventId(UUID.randomUUID().toString())
                .setVersion(0)
                .setCreated(UserCreated.newBuilder()
                    .setFirstName("test")
                    .setUsername("test")
                    .setLastName("test")
                    .build())
                .build()
            ),
            List.of()
        )
            .await()
            .atMost(Duration.ofSeconds(1));
    }
}
