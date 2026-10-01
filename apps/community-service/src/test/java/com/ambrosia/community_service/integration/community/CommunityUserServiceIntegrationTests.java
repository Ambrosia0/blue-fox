package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.UserCommunityService;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.community_service.utils.FollowCreator;
import com.ambrosia.community_service.utils.UserCreator;

@Transactional
public class CommunityUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired UserCommunityService userCommunityService;
    
    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired CommunityRepository communityRepository;

    @Autowired CommunityCreator communityCreator;

    @Autowired FollowCreator followCreator;

    @Autowired UserCreator userCreator;

    @Test
    void shouldReturnCommunityResponse() {
        var community = communityCreator.createCommunity(false);
        var response = userCommunityService.getCommunity(community.getSlug(), null);
        assertNotNull(response);
    }

    @Test
    void shouldReturnCommunityResponseWithFollow(){
        var community = communityCreator.createCommunity(false);
        var followedUser = userCreator.create();
        followCreator.create(community.getId(), followedUser.getId());
        var followed = userCommunityService
            .getCommunity(community.getSlug(), followedUser.getId())
            .getCommunityUserData()
            .isFollowed();
        assertNotNull(followed);
        assertTrue(followed);
    }
}
