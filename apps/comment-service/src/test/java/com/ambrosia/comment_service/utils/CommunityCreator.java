package com.ambrosia.comment_service.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import com.ambrosia.community_service.kafka_events.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.comment_service.community.model.entity.CommunityPermission;
import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;

@TestComponent
@Import(UserCreator.class)
public class CommunityCreator {
    @Autowired CommunityProjectionRepository communityProjectionRepository;
    @Autowired UserCreator userCreator;

    public CommunityProjection create(boolean isPrivate){
        var user = userCreator.create();
        var id = ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
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

    public CommunityProjection addPermission(CommunityProjection community, CommunityPermission permission){
        var updatedPermissions = new HashSet<>(community.getPermissions());
        updatedPermissions.add(permission);
        var updated = CommunityProjection.builder()
            .id(community.getId())
            .isNew(false)
            .isPrivate(community.isPrivate())
            .permissions(updatedPermissions)
            .build();
        
        communityProjectionRepository.process(
            List.of(CommunityEvent.newBuilder()
                .setId(community.getId())
                .setVersion(community.getVersion() + 1)
                .setEventId(UUID.randomUUID().toString())
                .setUpdate(CommunityUpdate.newBuilder()
                    .setName("test")
                    .setSlug("test_slug")
                    .setIsPrivate(community.isPrivate())
                    .putCommunityPermissions(
                        permission.getUserId().toString(),
                        ModeratorPermissions.newBuilder()
                            .addPermissions(Permission.valueOf(permission.getPermission()))
                            .build()
                    )
                )
                .build()),
            List.of()
        );
        return communityProjectionRepository.findById(community.getId()).get();
    }
}
