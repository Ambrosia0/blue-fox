package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.UserCommunityService;
import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.repository.CommunityFollowRepository;
import com.ambrosia.community_service.utils.Factory;

@Transactional
public class CommunityUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired UserCommunityService userCommunityService;
    
    @Autowired CommunityFollowRepository communityFollowRepository;

    @Autowired CommunityRepository communityRepository;

    @Test
    void shouldReturnCommunityResponse() {
        var community = createCommunity("test_community");
        var response = userCommunityService.getCommunity(community.getSlug(), null);
        assertNotNull(response);
    }

    @Test
    void shouldReturnCommunityResponseWithFollow(){
        var community = createCommunity("test_community");
        var followedUser = UUID.randomUUID();       
        createFollow(followedUser, community.getId());
        var followed = userCommunityService.getCommunity(community.getSlug(), followedUser).getCommunityUserData().isFollowed();
        assertNotNull(followed);
        assertTrue(followed);
    }

    private Community createCommunity(String name){
        var userId = UUID.randomUUID();
        return communityRepository.save(Factory.createCommunity(name, userId));
    }

    private CommunityFollow createFollow(UUID userId, long communityId){
        return communityFollowRepository.save(CommunityFollow.create(userId, communityId));
    }
}
