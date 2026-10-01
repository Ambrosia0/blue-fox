package com.ambrosia.community_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.entity.elastic.ElasticCommunity;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.repository.elastic.ElasticCommunityRepository;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.service.CommunitySearchService;
import com.ambrosia.community_service.community.service.UserCommunityService;
import com.ambrosia.community_service.utils.CommunityCreator;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;
import com.ambrosia.outbox.repository.SearchIndexOutboxRepository;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

public class ElasticCommunityIndexationIntegrationTests extends BaseIntegrationTest {
    @Autowired ElasticsearchOperations elasticsearchOperations;

    @Autowired UserCommunityService userCommunityService;
    @Autowired CommunitySearchService communitySearchService;

    @Autowired CommunityManageService communityManageService;

    @Autowired ElasticCommunityRepository elasticCommunityRepository;
    @Autowired CommunityRepository communityRepository;
    @Autowired SearchIndexOutboxRepository searchIndexOutboxRepository;
    
    @Autowired CommunityCreator communityCreator;

    @Autowired ElasticsearchOutboxRelay relay;

    @BeforeAll
    void init(){
        assertTrue(elasticsearchOperations.indexOps(ElasticCommunity.class).exists());
        elasticCommunityRepository.deleteAll();
        elasticsearchOperations.indexOps(ElasticCommunity.class).refresh();
    }

    @Test
    void shouldReturnCommunityPreviews() {
        var searched = List.of(
            communityCreator.createCommunity(false).getId(), 
            communityCreator.createCommunity(false).getId()
        );
        var filter = CommunityEventFilter.builder()
            .direction(Direction.DESC)
            .build();
            
        relay.flush();
        elasticsearchOperations.indexOps(ElasticCommunity.class).refresh();

        var search = communitySearchService.search(filter, 10);
        assertEquals(
            searched.size(), 
            communitySearchService.search(filter, 10)
                .stream()
                .filter(t -> searched.contains(t.id()))
                .count()
        );
        search.forEach(p -> assertNotNull(p.displayedName()));
    }
}