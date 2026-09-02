package com.ambrosia.community_service.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ambrosia.community_service.BaseIntegrationTest;
import com.ambrosia.community_service.community.model.dto.request.CommunityEventFilter;
import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.model.entity.elastic.ElasticCommunity;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.repository.elastic.ElasticCommunityRepository;
import com.ambrosia.community_service.community.service.CommunityManageService;
import com.ambrosia.community_service.community.service.CommunitySearchService;
import com.ambrosia.community_service.community.service.UserCommunityService;
import com.ambrosia.community_service.utils.Factory;
import com.ambrosia.outbox.repository.SearchIndexOutboxRepository;

import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

public class ElasticCommunityIndexationIntegrationTests extends BaseIntegrationTest {
    @Autowired ElasticsearchOperations elasticsearchOperations;

    @Autowired UserCommunityService userCommunityService;
    @Autowired CommunitySearchService communitySearchService;

    @Autowired CommunityManageService communityManageService;

    @Autowired ElasticCommunityRepository elasticCommunityRepository;
    @Autowired CommunityRepository communityRepository;
    @Autowired SearchIndexOutboxRepository searchIndexOutboxRepository;
    
    @BeforeAll
    void init(){
        assertTrue(elasticsearchOperations.indexOps(ElasticCommunity.class).exists());
        elasticCommunityRepository.deleteAll();
        elasticsearchOperations.indexOps(ElasticCommunity.class).refresh();
    }

    @Test
    void shouldReturnCommunityPreviews() {
        createTestCommunity("Test Comm 1");
        createTestCommunity("Test Comm 2");
        
        var filter = new CommunityEventFilter(null, null, null, null, null, Sort.Direction.DESC);
        await().pollInterval(Duration.ofSeconds(5)).atMost(Duration.ofSeconds(20))
            .untilAsserted(() -> {
                elasticsearchOperations.indexOps(ElasticCommunity.class).refresh();
                var search = communitySearchService.search(filter, 10);
                assertEquals(2, search.size());
                search.forEach(p -> assertNotNull(p.displayedName()));
            });
    }

    private CommunityResponse createTestCommunity(String name){
        var userId = UUID.randomUUID();
        var community = Factory.createCommunity(name, userId);
        var created = communityManageService.createCommunity(
            Factory.createRequest(community.getDisplayedName(), community.getSlug(), false),
            community.getOwnerId()
        );
        return created;
    }

    @AfterEach
    void cleanUp() {
        communityRepository.deleteAll();
        elasticCommunityRepository.deleteAll();
        elasticsearchOperations.indexOps(ElasticCommunity.class).refresh();
    }
}