package com.ambrosia.community_service.integration.follow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.exception.follow.DoesntFollowedException;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowService;
import com.ambrosia.community_service.kafka.producer.CommunityFollowEventProducer;
import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.FollowCreator;
import com.ambrosia.community_service.utils.UserCreator;

@Transactional
public class CommunityFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityFollowService communityFollowService;
    @MockitoSpyBean CommunityFollowEventProducer communityFollowEventProducer;
    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired FollowCreator followCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;

    @Test
    void shouldThrowAlreadyFollowedException(){
        var follow = followCreator.createFromScratch(false);
        assertThrows(
            AlreadyFollowedException.class, 
            () -> communityFollowService.followCommunity(follow.getId().communityId(), follow.getId().userId()));
    }

    @Test
    void shouldCreateCommunityFollowAndPublishEvent(){
        var community = communityCreator.createCommunity(false);
        var id = userCreator.create().getId();
        assertDoesNotThrow(() -> communityFollowService.followCommunity(community.getId(), id));
        verify(
            communityFollowEventProducer,
            times(1)
        ).on(any(CommunityFollowEvent.class));
        assertTrue(communityFollowRepository.findById(CommunityFollowKey.create(id, community.getId())).isPresent());
    }

    @Test
    void shouldThrowDoesntFollowedException(){
        var user = userCreator.create();
        assertThrows(
            DoesntFollowedException.class,
            () -> communityFollowService.removeFollow(
                ThreadLocalRandom.current().nextLong(), 
                user.getId()
            )
        );
    }

    @Test
    void shouldDeleteUserFollowAndPublishEvent(){
        var follow = followCreator.createFromScratch(false);
        assertDoesNotThrow(
            () -> communityFollowService.removeFollow(follow.getId().communityId(), follow.getId().userId()));
        verify(
            communityFollowEventProducer, 
            times(1)
        ).on(any(CommunityFollowEvent.class));
        assertFalse(communityFollowRepository
            .findById(CommunityFollowKey.create(follow.getId().userId(), follow.getId().communityId()))
            .isPresent()
        );
    }

    @Test
    void shouldReturnCommunityFollows(){
        var comm1 = communityCreator.createCommunity(false);
        var comm2 = communityCreator.createCommunity(false);
        var userId = userCreator.create().getId();
        followCreator.create(comm1.getId(), userId);
        followCreator.create(comm2.getId(), userId);
        assertEquals(2, communityFollowService.getFollows(userId, 0).getContent().size());        
    }
}
