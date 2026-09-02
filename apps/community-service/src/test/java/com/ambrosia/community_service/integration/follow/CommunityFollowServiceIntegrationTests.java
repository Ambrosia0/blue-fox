package com.ambrosia.community_service.integration.follow;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.exception.follow.AlreadyFollowedException;
import com.ambrosia.community_service.exception.follow.DoesntFollowedException;
import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowService;
import com.ambrosia.community_service.kafka.producer.CommunityFollowEventProducer;
import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;

@Transactional
public class CommunityFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityFollowService communityFollowService;
    @MockitoSpyBean CommunityFollowEventProducer communityFollowEventProducer;
    @Autowired CommunityFollowRepository communityFollowRepository;
    @Autowired CommunityRepository communityRepository;

    @Test
    void shouldThrowAlreadyFollowedException(){
        var follow = createFollow();
        assertThrows(
            AlreadyFollowedException.class, 
            () -> communityFollowService.followCommunity(follow.getId().communityId(), follow.getId().userId()));
    }

    @Test
    void shouldCreateCommunityFollowAndPublishEvent(){
        var community = createCommunity();
        var id = UUID.randomUUID();
        assertDoesNotThrow(() -> communityFollowService.followCommunity(community.getId(), id));
        verify(
            communityFollowEventProducer,
            times(1)
        ).on(any(CommunityFollowEvent.class));
        assertTrue(communityFollowRepository.findById(CommunityFollowKey.create(id, community.getId())).isPresent());
    }

    @Test
    void shouldThrowDoesntFollowedException(){
        assertThrows(
            DoesntFollowedException.class,
            () -> communityFollowService.removeFollow(
                ThreadLocalRandom.current().nextLong(), UUID.randomUUID())
        );
    }

    @Test
    void shouldDeleteUserFollowAndPublishEvent(){
        var follow = createFollow();
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
        var comm1 = createCommunity();
        var comm2 = createCommunity();
        var id = UUID.randomUUID();
        communityFollowRepository.save(CommunityFollow.create(id, comm1.getId()));
        communityFollowRepository.save(CommunityFollow.create(id, comm2.getId()));
        assertEquals(2, communityFollowService.getFollows(id, 0).getContent().size());        
    }

    private Community createCommunity(){
        var name = "TestCommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999L);
        return communityRepository.save(Community.builder()
            .displayedName(name)
            .slug(name)
            .ownerId(UUID.randomUUID())
            .build()
        );
    }

    private CommunityFollow createFollow(){
        var name = "TestCommunity"+ThreadLocalRandom.current().nextLong(1L, 999_999L);
        var community = communityRepository.save(Community.builder()
            .displayedName(name)
            .slug(name)
            .ownerId(UUID.randomUUID())
            .build()
        );
        return communityFollowRepository.save(CommunityFollow.create(UUID.randomUUID(), community.getId()));
    }
}
