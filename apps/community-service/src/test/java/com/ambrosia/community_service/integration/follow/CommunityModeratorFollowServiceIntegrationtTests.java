package com.ambrosia.community_service.integration.follow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;
import com.ambrosia.community_service.exception.follow.FollowRequestDoesntExist;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowRequestKey;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.follow.service.CommunityModeratorFollowService;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.CommunityFollowRequestCreator;

@Transactional
public class CommunityModeratorFollowServiceIntegrationtTests extends BaseIntegrationTest {
    @MockitoBean ScopeLinkService scopeLinkService;
    @Autowired CommunityModeratorFollowService communityModeratorFollowService;
    @Autowired CommunityFollowRequestRepository communityFollowRequestRepository;
    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired CommunityFollowRequestCreator communityFollowRequestCreator;
    @Autowired CommunityCreator communityCreator;

    @Test
    void shouldThrowNotEnoughPermissionsExceptionOnApprove() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(false);
        assertThrows(
                NotEnoughPermissionsException.class,
                () -> communityModeratorFollowService.approveRequest(
                        ThreadLocalRandom.current().nextLong(),
                        UUID.randomUUID(),
                        UUID.randomUUID()));
    }

    @Test
    void shouldThrowFollowRequestDoesntExistOnApprove() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(true);
        assertThrows(
                FollowRequestDoesntExist.class,
                () -> communityModeratorFollowService.approveRequest(
                        ThreadLocalRandom.current().nextLong(),
                        UUID.randomUUID(),
                        UUID.randomUUID()));
    }

    @Test
    void shouldApproveRequest() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(true);
        var request = communityFollowRequestCreator.create();
        assertDoesNotThrow(() -> communityModeratorFollowService.approveRequest(
                request.getId().communityId(),
                request.getId().userId(),
                UUID.randomUUID())
        );
        assertEquals(
                request.getId().communityId(),
                communityFollowRepository
                        .findById(CommunityFollowKey.create(request.getId().userId(), request.getId().communityId()))
                        .get().getId().communityId());
    }

    @Test
    void shouldThrowNotEnoughPermissionsOnDecline() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(false);
        var request = communityFollowRequestCreator.create();
        assertThrows(
                NotEnoughPermissionsException.class,
                () -> communityModeratorFollowService.declineRequest(
                    request.getId().communityId(), 
                    request.getId().userId(),
                    UUID.randomUUID()
                )
        );
    }

    @Test
    void shouldThrowFollowRequestDoesntExistOnDecline() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(true);
        var community = communityCreator.createCommunity(true);
        assertThrows(
            FollowRequestDoesntExist.class,
            () -> communityModeratorFollowService.declineRequest(
                community.getId(), 
                UUID.randomUUID(), 
                UUID.randomUUID()
            )
        );
    }

    @Test
    void shouldDeclineRequest() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(true);
        var request = communityFollowRequestCreator.create();
        assertDoesNotThrow(
                () -> communityModeratorFollowService.declineRequest(
                        request.getId().communityId(),
                        request.getId().userId(),
                        UUID.randomUUID()));
        assertFalse(
                communityFollowRequestRepository.findById(
                        CommunityFollowRequestKey.create(request.getId().userId(), request.getId().communityId()))
                        .isPresent());
    }

    @Test
    void shouldThrowNotEnoughPermissionsOnGetRequests() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(false);
        var community = communityCreator.createCommunity(true);
        assertThrows(
                NotEnoughPermissionsException.class,
                () -> communityModeratorFollowService.getRequests(
                        community.getId(),
                        UUID.randomUUID(),
                        0
                )
        );
    }

    @Test
    void shouldReturnRequests() {
        when(scopeLinkService.hasScope(anyLong(), any(ScopeEnum.class), any(UUID.class))).thenReturn(true);
        var community = communityCreator.createCommunity(true);

        communityFollowRequestCreator.create(community.getId());
        communityFollowRequestCreator.create(community.getId());
        communityFollowRequestCreator.create(community.getId());

        assertEquals(
                3,
                communityModeratorFollowService.getRequests(
                        community.getId(),
                        UUID.randomUUID(),
                        0
                ).size());
    }
}
