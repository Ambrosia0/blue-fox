package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.admin.AdminCommunityService;
import com.ambrosia.community_service.utils.CommunityCreator;
@Transactional
public class CommunityAdminServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired AdminCommunityService adminCommunityService;
    @Autowired CommunityCreator communityCreator;

    @Test
    void shouldReturnCommunities(){
        var communities = Arrays.asList(
            communityCreator.createCommunity(true).getId(), 
            communityCreator.createCommunity(false).getId(), 
            communityCreator.createCommunity(false).getId()
        );
        assertEquals(
            communities.size(), 
            adminCommunityService.getCommunities(PageRequest.of(0, 10))
                .getContent()
                .stream()
                .filter(t -> communities.contains(t.getId()))
                .count()
        );
    }
}
