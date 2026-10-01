package com.ambrosia.community_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.CommunitySearchService;
import com.ambrosia.community_service.utils.CommunityCreator;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles(profiles = "es-disabled", inheritProfiles = true)
public class PostgresCommunitySearchIntegrationTests extends BaseIntegrationTest {
    @Autowired CommunityRepository communityRepository;

    @Autowired CommunitySearchService communitySearchService;

    @Autowired CommunityCreator communityCreator;

    @Test
    void shouldReturnCommunityPreviews() {
        var community1 = communityCreator.createCommunity(false);
        var community2 = communityCreator.createCommunity(false);
        
        var filter = CommunityEventFilter.builder()
            .direction(Direction.DESC)
            .build();
        
        var searched = List.of(community1.getId(), community2.getId());
        var previews = communitySearchService.search(filter, 10);
        assertEquals(2, previews
                .stream()
                .filter(t -> searched.contains(t.id()))
                .count()
        );
        previews.forEach(p -> assertNotNull(p.displayedName()));
    }
}