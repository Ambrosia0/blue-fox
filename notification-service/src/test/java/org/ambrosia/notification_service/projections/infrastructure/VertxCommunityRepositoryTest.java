package org.ambrosia.notification_service.projections.infrastructure;

import com.ambrosia.community_service.kafka_events.*;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.utils.TestUtils;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxCommunityRepository;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@QuarkusTest
public class VertxCommunityRepositoryTest {
    @Inject
    VertxCommunityRepository vertxCommunityRepository;
    
    @Test
    void shouldNotThrowExceptionOnProcess(){
        vertxCommunityRepository.process(
            List.of(createEvent()),
            List.of(deleteEvent())
        )
            .await()
            .atMost(Duration.ofSeconds(3));
    }
    
    private CommunityEvent createEvent(){
        return CommunityEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setId(TestUtils.randLong())
            .setVersion(1)
            .setCreate(CommunityCreate.newBuilder()
                .setIsPrivate(false)
                .setSlug("test")
                .setName("test")
                .setOwnerId(UUID.randomUUID().toString())
                .putAllCommunityPermissions(
                    Map.of(UUID.randomUUID().toString(), ModeratorPermissions.newBuilder()
                        .addAllPermissions(List.of(Permission.COMMENT_DELETE))
                        .build()
                    )
                )
                .build())
            .build();
    }
    
    private CommunityEvent deleteEvent(){
        return CommunityEvent.newBuilder()
            .setEventId(UUID.randomUUID().toString())
            .setId(TestUtils.randLong())
            .setVersion(1)
            .setDelete(CommunityDelete.newBuilder()
                .build())
            .build();
    }
}
