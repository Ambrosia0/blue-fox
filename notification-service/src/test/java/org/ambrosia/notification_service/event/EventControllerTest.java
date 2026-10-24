package org.ambrosia.notification_service.event;

import static io.restassured.RestAssured.given;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import org.ambrosia.notification_service.event.api.EventController;
import org.ambrosia.notification_service.kafka_events.Notification;
import org.ambrosia.notification_service.kafka_events.TargetedNotification;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class EventControllerTest {
    
    @Inject
    EventController eventController;
    
    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;
    
    @TestSecurity(user = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", roles = "user")
    @OidcSecurity(
        claims = {
            @Claim(key = "sub", value = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        }
    )
    @Test
    void shouldSendUserEvent(){
        var sub = AssertSubscriber.create(10);
        
        var id = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        
        eventController.userEvents()
            .subscribe()
            .withSubscriber(sub);
        
        var message = createNotification(id);
        kafkaCompanion.produce(String.class, byte[].class)
            .fromRecords(new ProducerRecord<String, byte[]>(
                    "blog.notification",
                    0,
                    Instant.now().toEpochMilli(),
                    message.getTargetedNotification().getUserId(),
                    message.toByteArray()
                )
            )
            .awaitCompletion();
        
        Awaitility.await()
            .atMost(Duration.ofSeconds(12))
            .pollInterval(Duration.ofSeconds(1))
            .untilAsserted(() ->
                Assertions.assertTrue(() -> sub.getItems()
                    .stream()
                    .anyMatch(o -> {
                        try {
                            if(o instanceof OutboundSseEvent casted){
                                return casted.getData() != null &&
                                    casted.getData() instanceof String string &&
                                    string.equals(message.getTargetedNotification().getContent());
                            }
                            return false;
                        } catch (Exception ex){
                            throw new RuntimeException(ex);
                        }
                    })
                )
            );
        sub.cancel();
    }
    
    private Notification createNotification(UUID id){
        return Notification.newBuilder()
            .setTargetedNotification(TargetedNotification.newBuilder()
                .setContent("TestContent")
                .setUserId(id.toString())
                .build()
            )
            .build();
    }
}
