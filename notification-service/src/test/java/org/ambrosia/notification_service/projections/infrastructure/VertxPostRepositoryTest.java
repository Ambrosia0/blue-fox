package org.ambrosia.notification_service.projections.infrastructure;

import com.ambrosia.content_service.grpc.ReplyTo;
import com.ambrosia.content_service.grpc.User;
import com.ambrosia.content_service.kafka_events.PostCreated;
import com.ambrosia.content_service.kafka_events.PostDeleted;
import com.ambrosia.content_service.kafka_events.PostEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxPostRepository;
import org.ambrosia.notification_service.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class VertxPostRepositoryTest {
    @Inject
    VertxPostRepository vertxPostRepository;
    
    @Test
    void shouldNotThrowExceptionOnProcess(){
        vertxPostRepository.process(
            List.of(createEvent()),
            List.of(deleteEvent())
        )
            .await()
            .atMost(Duration.ofSeconds(3));
    }
    
    private PostEvent createEvent(){
        return PostEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setPostId(TestUtils.randLong())
            .setVersion(1)
            .setCreated(PostCreated.newBuilder()
                .setAuthor(User.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setUsername("test")
                    .setFirstName("test")
                    .setLastName("test")
                    .build()
                )
                .setTitle("test")
                .setPreview("test")
                .build()
            ).build();
    }
    
    private PostEvent deleteEvent(){
        return PostEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setPostId(TestUtils.randLong())
            .setVersion(1)
            .setDeleted(PostDeleted.newBuilder()
                .build())
            .build();
    }
}
