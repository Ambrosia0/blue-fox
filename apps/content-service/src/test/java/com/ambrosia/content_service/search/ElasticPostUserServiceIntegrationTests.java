package com.ambrosia.content_service.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.like.domain.repository.PostLikeRepository;
import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.EventFilter.SearchType;
import com.ambrosia.content_service.post.application.PostUserService;
import com.ambrosia.content_service.post.infrastructure.entity.PostElastic;
import com.ambrosia.content_service.search.infrastructure.PostIndexService;
import com.ambrosia.content_service.search.infrastructure.elastic.ElasticPostRepository;
import com.ambrosia.content_service.search.infrastructure.mappers.PostIndexMapper;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;

@Import({PostCreator.class, UserCreator.class})
public class ElasticPostUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired PostUserService postUserService;

    @Autowired PostLikeRepository postLikeRepository;

    @Autowired PostIndexService postIndexService;

    @Autowired ElasticPostRepository elasticPostRepository;

    @Autowired ElasticsearchOperations elasticsearchOperations;

    @Autowired ElasticsearchOutboxRelay relay;

    @Autowired PostCreator postCreator;

    @Autowired UserCreator userCreator;

    @Autowired PostIndexMapper postIndexMapper;

    @Test
    void shouldReturnPostPreviewsWithLatestSearchType() {
        postCreator.create(true);
        postCreator.create(true);
        postCreator.create(true);

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        relay.flush();
        elasticsearchOperations.indexOps(PostElastic.class).refresh();
        var search = postUserService.search(
                eventFilter, 
                Actor.builder()
                    .id(UUID.randomUUID())
                    .role(Role.USER)
                    .build(),
                    10
            );
        assertEquals(3, search.size());
        search.forEach(p -> assertEquals(0.0f, p.score()));
    }

    @Test
    void shouldReturnEmptyListWhenNoPostsFound() {
        var eventFilter = EventFilter.builder()
            .searchType(SearchType.POPULAR)
            .build();

        var response = postUserService.search(
                eventFilter, 
                Actor.builder()
                    .id(UUID.randomUUID())
                    .role(Role.USER)
                    .build(), 
                10
            );
        assertEquals(0, response.size());
    }

    @Test
    void shouldReturnPostPreviewsWithScore() {
        var post1 = postCreator.create(true);
        var post2 = postCreator.create(true);

        var searchingUser = userCreator.create();
        var eventFilter = EventFilter.builder()
            .searchType(SearchType.RELEVANCY)
            .searchString("test")
            .build();
        
        relay.flush();
        elasticsearchOperations.indexOps(PostElastic.class).refresh();
        var search = postUserService.search(
                eventFilter, 
                Actor.builder()
                    .id(searchingUser.getId())
                    .role(Role.USER)
                    .build(), 
                20
        );

        var searchedPosts = List.of(post1.getId(), post2.getId());
        assertEquals(2, search.stream()
            .filter(t -> searchedPosts.contains(t.postViewResponse().id()))
            .toList()
            .size()
        );
        search.forEach(p -> assertNotNull(p.score()));
    }

    @Test
    void shouldReturnPostPreviewsWithLikeStatus() {
        var post1 = postCreator.create(true);
        var post2 = postCreator.create(true);

        var searchingUser = userCreator.create();
        postLikeRepository.save(searchingUser.getId(), post1.getId());

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        relay.flush();

        elasticsearchOperations.indexOps(PostElastic.class).refresh();
        var search = postUserService.search(
            eventFilter, 
            Actor.builder()
                .id(searchingUser.getId())
                .role(Role.USER)
                .build(), 
            10
        );
        
        var searchedPosts = List.of(post1.getId(), post2.getId());
        assertEquals(2, search.stream()
            .filter(t -> searchedPosts.contains(t.postViewResponse().id()))
            .toList()
            .size()
        );
        var likedPost = search.stream()
            .filter(p -> p.postViewResponse().id() == post1.getId())
            .findFirst()
            .orElseThrow();
        assertEquals(true, likedPost.postViewResponse().isLiked());
    }

    @AfterEach
    void cleanUp() {
        postCreator.cleanUp();
        userCreator.cleanUp();
    }
}