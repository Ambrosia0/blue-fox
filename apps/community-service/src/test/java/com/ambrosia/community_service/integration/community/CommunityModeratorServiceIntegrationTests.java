package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.repository.CommunityBanRepository;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.service.CommunityModeratorService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.community.UserDoesntBannedException;
import com.ambrosia.community_service.exception.community.UserDoesntExistException;
import com.ambrosia.community_service.exception.community.UserIsModeratorException;
import com.ambrosia.community_service.grpc.ProfileService;

@Transactional
public class CommunityModeratorServiceIntegrationTests extends BaseIntegrationTest{
    @MockitoBean ProfileService profileService;
    @Autowired CommunityRepository communityRepository;
    @Autowired ScopeLinkRepository scopeLinkRepository;
    @Autowired CommunityModeratorService communityModeratorService;
    @Autowired CommunityBanRepository communityBanRepository;

    @BeforeEach
    void init(){
        when(profileService.isUserExists(any(UUID.class))).thenReturn(true);
    }

    @Test
    void shouldThrowUserDoesntExistException(){
        when(profileService.isUserExists(any(UUID.class))).thenReturn(false);
        var community = createUserWithScopesAndCommunity();
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
        var userId = UUID.randomUUID();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityModeratorService.banUser(
                ThreadLocalRandom.current().nextLong(),
                UUID.randomUUID(),
                userId,
                Instant.now()
            )
        );
    }

    @Test
    void shouldThrowUserIsModeratorException(){
        var community = createUserWithScopesAndCommunity();
        var addedUser = addUserToCommunityWithScopes(community.getId());
        assertThrows(
            UserIsModeratorException.class,
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                addedUser,
                Instant.now()
            )
        );
    }

    @Test
    void shouldBanUser(){
        var community = createUserWithScopesAndCommunity();
        var bannedUser = UUID.randomUUID();
        assertDoesNotThrow(
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                bannedUser,
                Instant.now().plus(Duration.ofDays(1))
            )
        );
        assertTrue(communityBanRepository.isBanned(bannedUser, community.getId()));
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnUnbanUser(){
        var community = createUserWithScopesAndCommunity();
        var userId = UUID.randomUUID();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityModeratorService.unbanUser(
                community.getId(),
                userId,
                UUID.randomUUID()
            )
        );
    }

    @Test
    void shouldThrowUserDoesntBannedException(){
        var community = createUserWithScopesAndCommunity();
        var userId = UUID.randomUUID();
        assertThrows(
            UserDoesntBannedException.class,
            () -> communityModeratorService.unbanUser(
                community.getId(),
                community.getOwnerId(),
                userId
            )
        );
    }

    @Test
    void shouldUnbanUser(){
        var community = createUserWithScopesAndCommunity();
        var userId = UUID.randomUUID();
        assertDoesNotThrow(
            () -> communityModeratorService.banUser(
                community.getId(),
                community.getOwnerId(),
                userId,
                Instant.now().plus(Duration.ofHours(4))
            )
        );
        assertTrue(communityBanRepository.isBanned(userId, community.getId()));
        assertDoesNotThrow(
            () -> communityModeratorService.unbanUser(
                community.getId(),
                community.getOwnerId(),
                userId
            )
        );
        assertFalse(communityBanRepository.isBanned(userId, community.getId()));
    }

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnScopesReceive(){
        var community = createUserWithScopesAndCommunity();
        var userId = UUID.randomUUID();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> communityModeratorService.getUsersScopesForCommunity(
                community.getId(), 
                userId
            )
        );
    }

    @Test
    void shouldReturnCommunityUsersScope(){
        var community = createUserWithScopesAndCommunity();
        var newModerator = addUserToCommunityWithScopes(community.getId());
        assertDoesNotThrow(
            () -> communityModeratorService.getUsersScopesForCommunity(
                community.getId(), 
                newModerator
            )
        );
    }

    private Community createCommunity(UUID ownerId){
        var name = "TestCommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999L);
        return communityRepository.save(Community.builder()
            .displayedName(name)
            .slug(name)
            .ownerId(ownerId)
            .build()
        ); 
    }

    private Community createUserWithScopesAndCommunity(){
        var userId = UUID.randomUUID();
        var community = createCommunity(userId);
        scopeLinkRepository.saveAll(
            Arrays.asList(ScopeEnum.values())
                .stream()
                .map(scope -> ScopeLink.create(userId, scope.getId(), community.getId()))
                .toList()
        );
        return community;
    }

    private UUID addUserToCommunityWithScopes(long communityId){
        var userId = UUID.randomUUID();
        scopeLinkRepository.saveAll(
            Arrays.asList(ScopeEnum.values())
                .stream()
                .map(scope -> ScopeLink.create(userId, scope.getId(), communityId))
                .toList()
        );
        return userId;
    }
}
