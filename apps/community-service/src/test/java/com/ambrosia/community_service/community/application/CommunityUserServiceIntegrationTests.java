package com.ambrosia.community_service.community.application;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.application.query.CommunityQueryService;
import com.ambrosia.community_service.community.domain.repository.CommunityRepository;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

@Transactional
public class CommunityUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired CommunityQueryService userCommunityService;
    
    @Autowired CommunityRepository communityRepository;

    @Autowired CommunityCreator communityCreator;

    @Autowired UserCreator userCreator;

    @Test
    void shouldReturnCommunityResponse() {
        var community = communityCreator.createCommunity(false);
        var response = userCommunityService.getCommunity(community.getSlug(), new Actor(null, Role.ANONYMOUS));
        assertNotNull(response);
    }

    @Test
    void shouldReturnCommunityResponseWithUserData(){
        var community = communityCreator.createCommunity(false);
        var followedUser = userCreator.create();
        var isFollowed = userCommunityService
            .getCommunity(
                community.getSlug(), 
                new Actor(followedUser.getId(), Role.USER)
            )
            .getCommunityUserData()
            .isFollowed();
        assertFalse(isFollowed);
    }
}
