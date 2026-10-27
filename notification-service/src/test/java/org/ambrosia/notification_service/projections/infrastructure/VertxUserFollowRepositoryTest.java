package org.ambrosia.notification_service.projections.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.UserFollowKey;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxUserFollowRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class VertxUserFollowRepositoryTest {
    @Inject
    VertxUserFollowRepository vertxUserFollowRepository;
    
    @Test
    void shouldNotThrowExceptionOnProcess(){
        vertxUserFollowRepository.applyChanges(
            List.of(createEvent()),
            List.of(deleteEvent())
        )
            .await()
            .atMost(Duration.ofSeconds(3));
    }
    
    private UserFollowKey createEvent(){
        return UserFollowKey.from(UUID.randomUUID(), UUID.randomUUID());
    }
    
    private UserFollowKey deleteEvent(){
        return UserFollowKey.from(UUID.randomUUID(), UUID.randomUUID());
    }
}
