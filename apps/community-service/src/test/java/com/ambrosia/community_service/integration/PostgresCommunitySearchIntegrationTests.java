package com.ambrosia.community_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.entity.Community;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.CommunitySearchService;
import com.ambrosia.community_service.utils.Factory;

import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles(profiles = "es-disabled", inheritProfiles = true)
public class PostgresCommunitySearchIntegrationTests extends BaseIntegrationTest {
    @Autowired CommunityRepository communityRepository;

    @Autowired CommunitySearchService communitySearchService;

    @Test
    void shouldReturnCommunityPreviews() {
        createCommunity("test_community1");
        createCommunity("test_community2");
        
        var filter = new CommunityEventFilter(null, null, null, null, null, Sort.Direction.DESC);
        
        var previews = communitySearchService.search(filter, 10);
        assertEquals(2, previews.size());
        previews.forEach(p -> assertNotNull(p.displayedName()));
    }

    private Community createCommunity(String name){
        var userId = UUID.randomUUID();
        return communityRepository.save(Factory.createCommunity(name, userId));
    }
}