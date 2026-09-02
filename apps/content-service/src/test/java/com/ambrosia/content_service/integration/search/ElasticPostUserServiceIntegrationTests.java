package com.ambrosia.content_service.integration.search;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.like.model.entity.PostLike;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.post.utils.TipTapPreviewConverter;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.SearchType;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.ElasticPostRepository;
import com.ambrosia.content_service.search.service.PostIndexService;

import tools.jackson.databind.ObjectMapper;

import com.ambrosia.content_service.util.Factory;
import com.ambrosia.content_service.util.PostTemplate;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;

public class ElasticPostUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired PostUserService postUserService;

    @Autowired PostRepository postRepository;

    @Autowired PostLikeRepository postLikeRepository;

    @Autowired PostIndexService postIndexService;

    @Autowired ElasticPostRepository elasticPostRepository;

    @Autowired ElasticsearchOperations elasticsearchOperations;

    @Autowired ElasticsearchOutboxRelay relay;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PreviewConverter previewConverter = new TipTapPreviewConverter(objectMapper, 50, 1);

    @Test
    void shouldReturnPostPreviewsWithLatestSearchType() {
        createTestPost();
        createTestPost();
        createTestPost();

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        relay.flush();
        elasticsearchOperations.indexOps(PostElastic.class).refresh();
        var search = postUserService.search(eventFilter, null, 10);
        assertEquals(3, search.size());
        search.forEach(p -> assertEquals(0.0f, p.score()));
    }

    @Test
    void shouldReturnEmptyListWhenNoPostsFound() {
        var eventFilter = EventFilter.builder()
            .searchType(SearchType.POPULAR)
            .build();

        var response = postUserService.search(eventFilter, null, 10);
        assertEquals(0, response.size());
    }

    @Test
    void shouldReturnPostPreviewsWithScore() {
        createTestPost();
        createTestPost();

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.RELEVANCY)
            .visible(true)
            .searchString("test")
            .build();
        
        relay.flush();
        elasticsearchOperations.indexOps(PostElastic.class).refresh();
        var search = postUserService.search(eventFilter, null, 10);
        assertEquals(2, search.size());
        search.forEach(p -> assertNotNull(p.score()));
    }

    @Test
    void shouldReturnPostPreviewsWithLikeStatus() {
        var post1 = createTestPost();
        createTestPost();

        var uuid = UUID.randomUUID();
        postLikeRepository.save(PostLike.create(uuid, post1.getId()));

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        relay.flush();

        elasticsearchOperations.indexOps(PostElastic.class).refresh();
        var search = postUserService.search(eventFilter, uuid, 10);
        assertEquals(2, search.size());
        var likedPost = search.stream()
            .filter(p -> p.postViewResponse().id() == post1.getId())
            .findFirst()
            .orElseThrow();
        assertEquals(true, likedPost.postViewResponse().isLiked());
    }

    private Post createTestPost(){
        var post = Factory.createTestPost();
        post.setPreview(previewConverter.convert(PostTemplate.template));
        post = postRepository.save(post);
        postIndexService.index(post);
        return post;
    }

    @AfterEach
    void cleanUp() {
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        elasticPostRepository.deleteAll();
        elasticsearchOperations.indexOps(PostElastic.class).refresh();
    }
}