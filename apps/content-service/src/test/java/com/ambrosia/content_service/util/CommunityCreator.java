package com.ambrosia.content_service.util;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

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
        communityProjectionRepository.insert(CommunityProjection.builder()
            .id(id)
            .name("TestCommunity"+id)
            .slug("test_slug"+id)
            .permissions(Arrays.asList(Permission.POST_DELETE)
                .stream()
                .map(t -> new CommunityPermission(t.name(), user.getId()))
                .collect(Collectors.toSet())
            )
            .isNew(true)
            .isPrivate(isPrivate)
            .build(),
            UUID.randomUUID()
        );
        return communityProjectionRepository.findById(id).get();
    }

    public void cleanUp(){
        communityProjectionRepository.deleteAll();
    }
}
