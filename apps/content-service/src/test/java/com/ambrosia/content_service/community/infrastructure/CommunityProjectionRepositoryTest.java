package com.ambrosia.content_service.community.infrastructure;

import com.ambrosia.community_service.kafka_events.*;
import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.content_service.user.model.entity.UserProjection;
import com.ambrosia.content_service.util.TestUtils;
import com.ambrosia.content_service.util.UserCreator;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
    
    @Autowired
    UserCreator userCreator;
    
    @Test
    void shouldCreateCommunityThenDeleteCommunity(){
        var user = userCreator.create();
        
        var event = createEvent(user);
        var id = event.getId();
        communityProjectionRepository.process(
            List.of(event),
            List.of()
        );
        
        var community = communityProjectionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Community doesn't created!"));
        
        Assertions.assertFalse(community.getPermissions().isEmpty());
        Assertions.assertFalse(community.getPermissions().stream()
            .filter(per -> per.getUserId().equals(user.getId()))
            .count() == 0
        );
        
        communityProjectionRepository.process(
            List.of(),
            List.of(deleteEvent(id, community.getVersion()))
        );
        
        Assertions.assertTrue(communityProjectionRepository.findById(id).isEmpty());
    }
    
    @Test
    void shouldUpsertCommunity(){
        var user = userCreator.create();
        var update = updateEvent(user);
        var id = update.getId();
        communityProjectionRepository.process(
            List.of(update),
            List.of()
        );
        
        Assertions.assertTrue(communityProjectionRepository.findById(id).isPresent());
    }
    
    @AfterEach
    void cleanUp(){
        communityProjectionRepository.deleteAll();
    }
    
    private CommunityEvent createEvent(UserProjection user){
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
                        k -> user.getId().toString(),
                        v -> v
                    ))
                )
                .setIsPrivate(false)
            )
            .build();
    }
    
    private CommunityEvent updateEvent(UserProjection user){
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
                        k -> user.getId().toString(),
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
