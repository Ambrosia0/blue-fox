package org.ambrosia.notification_service.projections.infrastructure;

import com.ambrosia.profile_service.kafka_events.UserCreated;
import com.ambrosia.profile_service.kafka_events.UserDeleted;
import com.ambrosia.profile_service.kafka_events.UserEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxUserRepository;
import org.ambrosia.notification_service.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class VertxUserRepositoryTest {
    @Inject
    VertxUserRepository vertxUserRepository;
    
    @Test
    void shouldNotThrowExceptionOnProcess(){
        vertxUserRepository.process(
            List.of(createEvent()),
            List.of(deleteEvent())
        )
            .await()
            .atMost(Duration.ofSeconds(3));
    }
    
    private UserEvent createEvent(){
        return UserEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setUserId(UUID.randomUUID().toString())
            .setVersion(1)
            .setCreated(UserCreated.newBuilder()
                .setFirstName("test")
                .setLastName("test")
                .setUsername("test")
                .setAvatarId("test")
                .build())
            .build();
    }
    
    private UserEvent deleteEvent(){
        return UserEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setUserId(UUID.randomUUID().toString())
            .setVersion(1)
            .setDeleted(UserDeleted.newBuilder()
                .build())
            .build();
    }
}
