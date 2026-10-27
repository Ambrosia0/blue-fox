package org.ambrosia.notification_service.projections.infrastructure;

import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.entity.key.CommunityFollowKey;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxCommunityFollowRepository;
import org.ambrosia.notification_service.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class VertxCommunityFollowRepositoryTest {
    @Inject
    VertxCommunityFollowRepository vertxCommunityFollowRepository;
    
    @Test
    void shouldNotThrowExceptionOnProcess(){
        vertxCommunityFollowRepository.process(
            List.of(createEvent()),
            List.of(deleteEvent())
        )
            .await()
            .atMost(Duration.ofSeconds(3));
    }
    
    private CommunityFollowKey createEvent(){
        return CommunityFollowKey.from(UUID.randomUUID(), TestUtils.randLong());
    }

    private CommunityFollowKey deleteEvent(){
        return CommunityFollowKey.from(UUID.randomUUID(), TestUtils.randLong());
    }
}
