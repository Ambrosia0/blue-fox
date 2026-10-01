package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.repository.CommunityBanRepository;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.CommunityModeratorService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntBannedException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsModeratorException;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.UserCreator;

@Transactional
public class CommunityModeratorServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityModeratorService communityModeratorService;
    @Autowired CommunityBanRepository communityBanRepository;

    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;

    @Test
    void shouldThrowUserDoesntExistException(){
        var community = communityCreator.createCommunity(false);
        assertThrows(
            UserDoesntExistException.class,
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                UUID.randomUUID(),
                Instant.now()
            )
        );
    }

    @Test
    void shouldThrowNotEnoughPermissionExceptionOnBanUser(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        var userToBan = userCreator.create();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityModeratorService.banUser(
                community.getId(),
                user.getId(),
                userToBan.getId(),
                Instant.now()
            )
        );
    }

    @Test
    void shouldThrowUserIsModeratorException(){
        var community = communityCreator.createCommunity(false);
        var addedUser = userCreator.create();
        community.replaceScopes(
            Set.of(ScopeLink.create(addedUser.getId(), ScopeEnum.USER_BAN.getId()))
        );
        communityRepository.save(community);
        assertThrows(
            UserIsModeratorException.class,
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                addedUser.getId(),
                Instant.now()
            )
        );
    }

    @Test
    void shouldBanUser(){
        var community = communityCreator.createCommunity(false);
        var userToBan = userCreator.create();
        assertDoesNotThrow(
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                userToBan.getId(),
                Instant.now().plus(Duration.ofDays(1))
            )
        );
        assertTrue(communityBanRepository.isBanned(userToBan.getId(), community.getId()));
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnUnbanUser(){
        var community = communityCreator.createCommunity(false);
        var userToBan = userCreator.create();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityModeratorService.unbanUser(
                community.getId(),
                UUID.randomUUID(),
                userToBan.getId()
            )
        );
    }

    @Test
    void shouldThrowUserDoesntBannedException(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        assertThrows(
            UserDoesntBannedException.class,
            () -> communityModeratorService.unbanUser(
                community.getId(),
                community.getOwnerId(),
                user.getId()
            )
        );
    }

    @Test
    void shouldUnbanUser(){
        var community = communityCreator.createCommunity(false);
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                user.getId(),
                Instant.now().plus(Duration.ofHours(4))
            )
        );
        assertTrue(communityBanRepository.isBanned(user.getId(), community.getId()));
        assertDoesNotThrow(
            () -> communityModeratorService.unbanUser(
                community.getId(),
                community.getOwnerId(),
                user.getId()
            )
        );
        assertFalse(communityBanRepository.isBanned(user.getId(), community.getId()));
    }

}
