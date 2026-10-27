package com.ambrosia.content_service.util;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.ambrosia.community_service.kafka_events.CommunityCreate;
import com.ambrosia.community_service.kafka_events.CommunityEvent;
import com.ambrosia.community_service.kafka_events.ModeratorPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.community_service.kafka_events.Permission;
import com.ambrosia.content_service.community.model.entity.CommunityPermission;
import com.ambrosia.content_service.community.model.entity.CommunityProjection;
import com.ambrosia.content_service.community.repository.CommunityProjectionRepository;

@TestComponent 
@Import(UserCreator.class)
public class CommunityCreator {
    @Autowired CommunityProjectionRepository communityProjectionRepository;

    @Autowired UserCreator userCreator;

    public CommunityProjection create(boolean isPrivate){
        var user = userCreator.create();
        var id = ThreadLocalRandom.current().nextLong();
        communityProjectionRepository.process(
            List.of(CommunityEvent.newBuilder()
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
                    .setIsPrivate(isPrivate)
                )
                    .build()),
            List.of()
        );
        return communityProjectionRepository.findById(id).get();
    }

    public void cleanUp(){
        communityProjectionRepository.deleteAll();
    }
}