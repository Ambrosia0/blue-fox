package com.ambrosia.community_service.community.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.domain.repository.CommunityRepository;
import com.ambrosia.community_service.community.infrastructure.persistence.JdbcCommunityBanRepository;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

@Transactional
public class CommunityModeratorServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityModeratorService communityModeratorService;
    @Autowired JdbcCommunityBanRepository communityBanRepository;

    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;


    @Test
    void shouldBanUser(){
        var community = communityCreator.createCommunity(false);
        var userToBan = userCreator.create();
        assertDoesNotThrow(
            () -> communityModeratorService.banUser(
                community.getId(),
                new Actor(community.getOwnerId(), Role.USER),
                userToBan.getId(),
                Instant.now().plus(Duration.ofDays(1))
            )
        );
        assertFalse(communityBanRepository.findById(userToBan.getId(), community.getId())
            .isEmpty()
        );
    }

    @Test
    void shouldUnbanUser(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> communityModeratorService.banUser(
                community.getId(),
                new Actor(community.getOwnerId(), Role.USER),
                user.getId(),
                Instant.now().plus(Duration.ofHours(4))
            )
        );
        assertFalse(communityBanRepository.findById(user.getId(), community.getId()).isEmpty());
        assertDoesNotThrow(
            () -> communityModeratorService.unbanUser(
                community.getId(),
                new Actor(community.getOwnerId(), Role.USER),
                user.getId()
            )
        );
        assertTrue(communityBanRepository.findById(user.getId(), community.getId()).isEmpty());
    }

}
