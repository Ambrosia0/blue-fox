package org.ambrosia.notification_service.event;

import com.ambrosia.comment_service.kafka_events.CommentCreated;
import com.ambrosia.comment_service.kafka_events.CommentEvent;
import com.ambrosia.comment_service.kafka_events.User;
import com.google.protobuf.util.JsonFormat;
import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.oidc.Claim;
import io.quarkus.test.security.oidc.OidcSecurity;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.helpers.test.AssertSubscriber;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import jakarta.inject.Inject;
import jakarta.ws.rs.sse.OutboundSseEvent;
import org.ambrosia.notification_service.utils.TestUtils;
import org.ambrosia.notification_service.core.policy.Actor;
import org.ambrosia.notification_service.core.policy.PolicyContext;
import org.ambrosia.notification_service.event.api.PostEventController;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class PostEventControllerTest {
    @Inject
    PostEventController postEventController;
    
    @InjectMock
    PolicyContext policyContext;
    
    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;
    
    @TestSecurity(user = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa", roles = "user")
    @OidcSecurity(
        claims = {
            @Claim(key = "sub", value = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        }
    )
    @Test
    void shouldSendPostEvent(){
        Mockito.when(policyContext.validate(
                    Mockito.any(Actor.class),
                    Mockito.any(),
                    Mockito.any()
                ))
            .thenReturn(Uni.createFrom().voidItem());
        var sub = AssertSubscriber.create(10);
        var postId = TestUtils.randLong();
        
        postEventController.consume(postId)
            .subscribe()
            .withSubscriber(sub);
        
        var message = createCommentEvent(postId);
        kafkaCompanion.produce(String.class, byte[].class)
            .fromRecords(new ProducerRecord<String, byte[]>(
                "blog.comment",
                0,
                Instant.now().toEpochMilli(),
                Long.toString(message.getPostId()),
                message.toByteArray()
            ))
            .awaitCompletion();
        
        Awaitility.await()
            .atMost(Duration.ofSeconds(20))
            .pollInterval(Duration.ofSeconds(1))
            .untilAsserted(() ->
                Assertions.assertTrue(sub.getItems()
                    .stream()
                    .anyMatch(o -> {
                        try {
                            if(o instanceof OutboundSseEvent event) {
                                return event.getData() instanceof String string &&
                                    string.equals(JsonFormat.printer().print(message));
                            }
                            return false;
                        } catch (Exception ex){
                            throw new RuntimeException(ex);
                        }
                    }))
            );
        sub.cancel();
    }
    
    CommentEvent createCommentEvent(Long postId){
        return CommentEvent.newBuilder()
            .setCommentId(ThreadLocalRandom.current().nextLong(1L, 999_999L))
            .setEventId(UUID.randomUUID().toString())
            .setPostId(postId)
            .setCreated(CommentCreated.newBuilder()
                .setContent("test_content")
                .setUser(User.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setFirstName("test_name")
                    .setLastName("test_name")
                    .setUsername("test_name")
                    .build())
                .setCreatedAt(Instant.now().toEpochMilli())
                .build()
            )
            .build();
    }
}
