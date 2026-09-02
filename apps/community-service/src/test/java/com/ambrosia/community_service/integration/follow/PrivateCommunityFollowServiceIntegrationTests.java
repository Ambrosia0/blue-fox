package com.ambrosia.community_service.integration.follow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowRequestKey;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowService;
import com.ambrosia.community_service.utils.Factory;

@Transactional
public class PrivateCommunityFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired CommunityFollowRepository communityFollowRepository;
    @Autowired CommunityFollowRequestRepository communityFollowRequestRepository;
    @Autowired CommunityFollowService communityFollowService;

    @Test
    void shouldThrowAlreadyFollowedException(){
        var follow = createFollow();
        assertThrows(
            AlreadyFollowedException.class,
            () -> communityFollowService.followCommunity(
                follow.getId().communityId(),
                follow.getId().userId()
            )
        );
    }

    @Test
    void shouldThrowCommunityDoesntExists(){
        assertThrows(
            CommunityDoesntExistException.class, 
            () -> communityFollowService.followCommunity(
                ThreadLocalRandom.current().nextLong(),
                UUID.randomUUID()
            )
        );
    }

    @Test
    void shouldCreateFollowRequest(){
        var community = createPrivateCommunity();
        var id = UUID.randomUUID();
        assertDoesNotThrow(() -> communityFollowService.followCommunity(community.getId(), id));
        assertEquals(
            community.getId(), 
            communityFollowRequestRepository.findById(CommunityFollowRequestKey.create(id, community.getId()))
                .get().getId().communityId());
    }

    private CommunityFollow createFollow(){
        var community = createPrivateCommunity();
        return communityFollowRepository.save(CommunityFollow.create(UUID.randomUUID(), community.getId()));
    }

    private Community createPrivateCommunity(){
        return communityRepository.save(Factory.createPrivateCommunity());
    }
}
