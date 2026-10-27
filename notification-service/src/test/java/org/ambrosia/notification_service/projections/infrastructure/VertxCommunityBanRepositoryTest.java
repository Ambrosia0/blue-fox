package org.ambrosia.notification_service.projections.infrastructure;

import com.ambrosia.community_service.kafka_events.CommunityBan;
import com.ambrosia.community_service.kafka_events.CommunityBanEvent;
import com.ambrosia.community_service.kafka_events.CommunityUnban;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.ambrosia.notification_service.projections.infrastructure.persistence.impl.VertxCommunityBanRepository;
import org.ambrosia.notification_service.utils.TestUtils;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@QuarkusTest
public class VertxCommunityBanRepositoryTest {
    @Inject
    VertxCommunityBanRepository vertxCommunityBanRepository;
    
    @Test
    void shouldNotThrowExceptionOnProcess(){
        vertxCommunityBanRepository.process(
            List.of(createEvent()),
            List.of(deleteEvent())
        )
            .await()
            .atMost(Duration.ofSeconds(3));
    }
    
    private CommunityBanEvent createEvent(){
        return CommunityBanEvent.newBuilder()
            .setBan(CommunityBan.newBuilder()
                .setBeforeDate(Instant.now().plus(Duration.ofDays(1)).toEpochMilli())
                .setCommunityId(TestUtils.randLong())
                .setUserId(UUID.randomUUID().toString())
                .build())
            .build();
    }
    
    private CommunityBanEvent deleteEvent(){
        return CommunityBanEvent.newBuilder()
            .setUnban(CommunityUnban.newBuilder()
                .setCommunityId(TestUtils.randLong())
                .setUserId(UUID.randomUUID().toString())
                .build())
            .build();
    }
}
