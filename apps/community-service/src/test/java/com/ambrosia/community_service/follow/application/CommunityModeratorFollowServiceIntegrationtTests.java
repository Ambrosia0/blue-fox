package com.ambrosia.community_service.follow.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;
import com.ambrosia.community_service.follow.domain.repository.CommunityFollowRepository;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

@Transactional
public class CommunityModeratorFollowServiceIntegrationtTests extends BaseIntegrationTest {
    @Autowired CommunityModeratorFollowService communityModeratorFollowService;

    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired CommunityCreator communityCreator;

	@Autowired UserCreator userCreator;

    @Test
    void shouldApproveRequest() {
        var community = communityCreator.createCommunity(true);

        var requestingUser = userCreator.create();

		var follow = CommunityFollow.buidler()
			.communityId(community.getId())
			.userId(requestingUser.getId())
            .requiresApproval(community.isPrivate())
			.build();

        communityFollowRepository.persist(follow);

        assertDoesNotThrow(
			() -> communityModeratorFollowService.approveRequest(
                community.getId(),
                requestingUser.getId(),
                new Actor(community.getOwnerId(), Role.USER)
			)
        );
        assertDoesNotThrow(() ->{
			var res = communityFollowRepository.findById(requestingUser.getId(), community.getId())
				.get();
			assertEquals(CommunityFollow.FollowState.FOLLOWED, res.getState());
		});
    }

    @Test
    void shouldDeclineRequest() {
        var community = communityCreator.createCommunity(true);

		var requestingUser = userCreator.create();

		var follow = CommunityFollow.buidler()
			.communityId(community.getId())
			.userId(requestingUser.getId())
            .requiresApproval(community.isPrivate())
			.build();
            
		communityFollowRepository.persist(follow);

        assertDoesNotThrow(
            () -> communityModeratorFollowService.declineRequest(
                    community.getId(),
                    requestingUser.getId(),
                    new Actor(community.getOwnerId(), Role.USER)
			)
		);
        assertTrue(communityFollowRepository.findById(requestingUser.getId(), community.getId()).isEmpty());
    }
}
