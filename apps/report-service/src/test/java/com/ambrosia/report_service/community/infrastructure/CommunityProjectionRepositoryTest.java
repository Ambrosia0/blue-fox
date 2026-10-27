package com.ambrosia.report_service.community.infrastructure;

import com.ambrosia.community_service.kafka_events.*;
import com.ambrosia.report_service.BaseIntegrationTest;
import com.ambrosia.report_service.community.repository.CommunityProjectionRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CommunityProjectionRepositoryTest extends BaseIntegrationTest {
    @Autowired
    CommunityProjectionRepository communityProjectionRepository;
    
    @Test
    void shouldCreateCommunityThenDeleteCommunity(){
        var event = createEvent();
        communityProjectionRepository.process(
            List.of(event),
            List.of()
        );
        
        Assertions.assertTrue(communityProjectionRepository.existsById(event.getId()));
        
        communityProjectionRepository.process(
            List.of(),
            List.of(deleteEvent(event.getId(), event.getVersion()))
        );
        
        Assertions.assertFalse(communityProjectionRepository.existsById(event.getId()));
    }
    
    @Test
    void shouldUpsertCommunity(){
        var update = updateEvent();
        communityProjectionRepository.process(
            List.of(update),
            List.of()
        );
        
        Assertions.assertTrue(communityProjectionRepository.existsById(update.getId()));
    }
    
    private CommunityEvent createEvent(){
        var id = ThreadLocalRandom.current().nextLong();
        return CommunityEvent.newBuilder()
            .setId(id)
            .setVersion(0)
            .setEventId(UUID.randomUUID().toString())
            .setCreate(CommunityCreate.newBuilder()
                .setName("TestCommunity"+id)
                .setSlug("test_slug"+id)
                .putAllCommunityPermissions(Arrays.asList(Permission.POST_DELETE)
                    .stream()
                    .map(t -> ModeratorPermissions.newBuilder()
                        .addAllPermissions(List.of(t))
                        .build()
                    )
                    .collect(Collectors.toMap(
                        k -> UUID.randomUUID().toString(),
                        v -> v
                    ))
                )
                .setIsPrivate(false)
            )
            .build();
    }
    
    private CommunityEvent updateEvent(){
        var id = ThreadLocalRandom.current().nextLong();
        return CommunityEvent.newBuilder()
            .setId(id)
            .setVersion(0)
            .setEventId(UUID.randomUUID().toString())
            .setCreate(CommunityCreate.newBuilder()
                .setName("TestCommunity"+id)
                .setSlug("test_slug"+id)
                .putAllCommunityPermissions(Arrays.asList(Permission.POST_DELETE)
                    .stream()
                    .map(t -> ModeratorPermissions.newBuilder()
                        .addAllPermissions(List.of(t))
                        .build()
                    )
                    .collect(Collectors.toMap(
                        k -> UUID.randomUUID().toString(),
                        v -> v
                    ))
                )
                .setIsPrivate(false)
            )
            .build();
    }
    
    private CommunityEvent deleteEvent(Long id, Long version){
        return CommunityEvent.newBuilder()
            .setId(id)
            .setVersion(version)
            .setEventId(UUID.randomUUID().toString())
            .setDelete(CommunityDelete.newBuilder().build())
            .build();
    }
}
