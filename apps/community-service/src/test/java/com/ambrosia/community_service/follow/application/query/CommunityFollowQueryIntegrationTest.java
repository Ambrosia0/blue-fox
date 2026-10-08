package com.ambrosia.community_service.follow.application.query;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRepository;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRequestRepository;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

public class CommunityFollowQueryIntegrationTest extends BaseIntegrationTest{
    @Autowired CommunityCreator communityCreator;

    @Autowired UserCreator userCreator;

    @Autowired CommunityFollowQueryService communityFollowQueryService;

    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired CommunityFollowRequestRepository communityFollowRequestRepository;

    @Test 
    void shouldReturnUserFollows(){
        var community1 = communityCreator.createCommunity(true);
        var community2 = communityCreator.createCommunity(false);

        var user = userCreator.create();
        var follow = CommunityFollow.buidler()
            .userId(user.getId())
            .communityId(community1.getId())
            .requiresApproval(community1.isPrivate())
            .build();

        communityFollowRepository.persist(follow);
        communityFollowRequestRepository.approve(user.getId(),community1.getId());

        follow = CommunityFollow.buidler()
            .userId(user.getId())
            .communityId(community2.getId())
            .requiresApproval(community2.isPrivate())
            .build();

        communityFollowRepository.persist(follow);

        assertDoesNotThrow(
            () ->{
                var res = communityFollowQueryService.getUserFollows(
                    new Actor(user.getId(), Role.USER),
                    FollowFilter.builder().build(), 
                    20
                );
                assertEquals(2, res.getContent().size());
            }
        );
    }

    @Test 
    void shouldReturnCommunityFollowRequests(){
        var community = communityCreator.createCommunity(true);

        var user1 = userCreator.create();
        var user2 = userCreator.create();

        var follow = CommunityFollow.buidler()
            .userId(user1.getId())
            .requiresApproval(community.isPrivate())
            .communityId(community.getId())
            .build();

        communityFollowRepository.persist(follow);

        follow = CommunityFollow.buidler()
            .userId(user2.getId())
            .communityId(community.getId())
            .requiresApproval(community.isPrivate())
            .build();

        communityFollowRepository.persist(follow);

        assertDoesNotThrow(
            () -> {
                var res = communityFollowQueryService.getCommunityFollows(
                    new Actor(community.getOwnerId(), Role.USER),
                    community.getId(),
                    FollowFilter.builder().build(),
                    20
                );
                assertEquals(2, res.getContent().size());
            }
        );
    }
}
