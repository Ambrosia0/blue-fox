package com.ambrosia.comment_service.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.comment_service.community.model.entity.CommunityPermission;
import com.ambrosia.comment_service.community.model.entity.CommunityProjection;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.community_service.kafka_events.Permission;

@TestComponent
@Import(UserCreator.class)
public class CommunityCreator {
    @Autowired CommunityProjectionRepository communityProjectionRepository;
    @Autowired UserCreator userCreator;

    public CommunityProjection create(boolean isPrivate){
        var user = userCreator.create();
        var id = ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
        communityProjectionRepository.insert(CommunityProjection.builder()
            .id(id)
            .isNew(true)
            .permissions(Arrays.asList(Permission.COMMENT_DELETE)
                .stream()
                .map(t -> new CommunityPermission(t.name(), user.getId()))
                .collect(Collectors.toSet())
            )
            .isPrivate(isPrivate)
            .build(),
            UUID.randomUUID()
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
        communityProjectionRepository.update(
            updated,
            UUID.randomUUID()
        );
        return communityProjectionRepository.findById(community.getId()).get();
    }
}
