package com.ambrosia.community_service.integration.follow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowRequestKey;
import com.ambrosia.community_service.follow.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowService;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.FollowCreator;
import com.ambrosia.community_service.utils.UserCreator;

@Transactional
public class PrivateCommunityFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityFollowRequestRepository communityFollowRequestRepository;
    @Autowired CommunityFollowService communityFollowService;

    @Autowired FollowCreator followCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;

    @Test
    void shouldThrowAlreadyFollowedException(){
        var follow = followCreator.createFromScratch(true);
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
        var user = userCreator.create();
        assertThrows(
            CommunityDoesntExistException.class, 
            () -> communityFollowService.followCommunity(
                ThreadLocalRandom.current().nextLong(),
                user.getId()
            )
        );
    }

    @Test
    void shouldCreateFollowRequest(){
        var community = communityCreator.createCommunity(true);
        var user = userCreator.create();
        assertDoesNotThrow(() -> communityFollowService.followCommunity(community.getId(), user.getId()));
        assertEquals(
            community.getId(), 
            communityFollowRequestRepository.findById(CommunityFollowRequestKey
                .create(user.getId(), community.getId())
            )
                .get()
                .getId()
                .communityId()
        );
    }
}
