package com.ambrosia.community_service.integration.community;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.service.admin.AdminCommunityService;
import com.ambrosia.community_service.community.utils.ScopeEnum;
@Transactional
public class CommunityAdminServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired CommunityRepository communityRepository;
    @Autowired ScopeLinkRepository scopeLinkRepository;
    @Autowired AdminCommunityService adminCommunityService;

    @Test
    void shouldReturnCommunities(){
        createCommunity();
        createCommunity();
        createCommunity();
        assertEquals(
            3, 
            adminCommunityService.getCommunities(PageRequest.of(0, 10))
                .getContent().size()
        );
    }

    private Community createCommunity(){
        var num = ThreadLocalRandom.current().nextLong(1L, 999_999L);
        var community = communityRepository.save(Community.builder()
            .displayedName("Test community"+num)
            .slug("test_community"+num)
            .ownerId(UUID.randomUUID())
            .build()
        );

        scopeLinkRepository.saveAll(
            Arrays.asList(ScopeEnum.values())
                .stream()
                .map(scope -> ScopeLink.create(community.getOwnerId(), scope.getId(), community.getId()))
                .toList()
        );
        return community;
    }

}
