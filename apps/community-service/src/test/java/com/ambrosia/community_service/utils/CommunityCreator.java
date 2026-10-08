package com.ambrosia.community_service.utils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.context.annotation.Import;

import com.ambrosia.community_service.community.application.CommunityIndexService;
import com.ambrosia.community_service.community.domain.entity.Community;
import com.ambrosia.community_service.community.domain.entity.CommunityBan;
import com.ambrosia.community_service.community.domain.repository.CommunityBanRepository;
import com.ambrosia.community_service.community.domain.repository.CommunityRepository;
import com.ambrosia.community_service.community.infrastructure.elastic.ElasticCommunityRepository;
import com.ambrosia.community_service.community.infrastructure.persistence.JdbcCommunityRepository;

@Import({UserCreator.class})
@TestComponent
public class CommunityCreator {
    @Autowired CommunityRepository communityRepository;

    @Autowired JdbcCommunityRepository jdbcCommunityRepository;

    @Autowired UserCreator userCreator;

    @Autowired CommunityBanRepository communityBanRepository;

    @Autowired(required = false) ElasticCommunityRepository elasticCommunityRepository;

    @Autowired CommunityIndexService communityIndexService;

    public Community createCommunity(boolean isPrivate){
        var name = "testcommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
        var user = userCreator.create();
        var community = communityRepository.save(
            Community.builder()
                .displayedName(name)
                .slug(name)
                .ownerId(user.getId())
                .tags(Set.of("#tags"))
                .isPrivate(isPrivate)
                .build()
        );
        communityIndexService.index(community);
        return community;
    }

    public Community createCommunity(UUID ownerId, boolean isPrivate){
        var name = "testcommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999_999L);
        var community = communityRepository.save(Community.builder()
            .displayedName(name)
            .slug(name)
            .ownerId(ownerId)
            .tags(Set.of("#tags"))
            .isPrivate(isPrivate)
            .build()
        );
        communityIndexService.index(community);
        return community;
    }

    public CommunityBan createBan(Long communityId, UUID userToBan){
        return communityBanRepository.save(
            CommunityBan.create(
                userToBan, 
                communityId, 
                Instant.now().plus(5, ChronoUnit.DAYS)
            )
        );
    }


    public void cleanUp(){
        jdbcCommunityRepository.deleteAll();
        userCreator.cleanUp();
        if(elasticCommunityRepository != null)
            elasticCommunityRepository.deleteAll();
    }
}
