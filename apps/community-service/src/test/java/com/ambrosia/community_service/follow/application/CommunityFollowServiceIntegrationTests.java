package com.ambrosia.community_service.follow.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow.FollowState;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRepository;
import com.ambrosia.community_service.infrastructure.kafka.producer.CommunityFollowEventProducer;
import com.ambrosia.community_service.kafka_events.CommunityFollowEvent;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

@Transactional
public class CommunityFollowServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityFollowService communityFollowService;
    @MockitoSpyBean CommunityFollowEventProducer communityFollowEventProducer;

    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;

    @Test
    void shouldCreateCommunityFollowAndPublishEvent(){
        var community = communityCreator.createCommunity(false);
        var id = userCreator.create().getId();
        assertDoesNotThrow(
            () -> communityFollowService.followCommunity(community.getId(), new Actor(id, Role.USER))
        );
        verify(
            communityFollowEventProducer,
            times(1)
        ).on(any(CommunityFollowEvent.class));
        assertTrue(communityFollowRepository.findById(id, community.getId()).isPresent());
    }

    @Test
    void shouldDeleteUserFollowAndPublishEvent(){
        var id = userCreator.create().getId();
        var community = communityCreator.createCommunity(false);
        var follow = CommunityFollow.buidler()
            .communityId(community.getId())
            .userId(id)
            .requiresApproval(community.isPrivate())
            .build();

        communityFollowRepository.persist(follow);
        assertDoesNotThrow(
            () -> communityFollowService.removeFollow(
                community.getId(), 
                new Actor(id, Role.USER)
            )
        );
        verify(
            communityFollowEventProducer, 
            times(1)
        ).on(any(CommunityFollowEvent.class));
        assertFalse(communityFollowRepository
            .findById(id, community.getId())
            .isPresent()
        );
    }

    @Test 
    void shouldCreateFollowRequest(){
        var community = communityCreator.createCommunity(true);
        var id = userCreator.create().getId();
        assertDoesNotThrow(
            () -> communityFollowService.followCommunity(community.getId(), new Actor(id, Role.USER))
        );
        assertTrue(communityFollowRepository.findById(id, community.getId()).get().getState() == FollowState.REQUESTED);
    }
}
