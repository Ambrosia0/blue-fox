package com.ambrosia.content_service.integration.post;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.core.PreviewConverter;
import com.ambrosia.content_service.like.model.entity.PostLike;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.post.utils.TipTapPreviewConverter;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.SearchType;
import com.ambrosia.content_service.search.model.dto.EventFilter.SortField;
import com.ambrosia.content_service.search.repository.DocumentVectorRepository;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.util.Factory;
import com.ambrosia.content_service.util.PostTemplate;

import tools.jackson.databind.ObjectMapper;

@Transactional
@ActiveProfiles(profiles = {"es-disabled"}, inheritProfiles = true)
public class PostUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired PostUserService postUserService;

    @Autowired PostRepository postRepository;

    @Autowired PostLikeRepository postLikeRepository;

    @Autowired DocumentVectorRepository documentVectorRepository;

    @Autowired PostIndexService postIndexService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PreviewConverter previewConverter = new TipTapPreviewConverter(objectMapper, 50, 1);

    @Test
    void shouldThrowPostDoesntExistException() {
        assertThrows(
            PostDoesntExistException.class,
            () -> postUserService.getPost(999L, null)
        );
    }

    @Test
    void shouldReturnPostWithoutLikeWhenUserIsNull() {
        var post = createTestPost();
        var response = postUserService.getPost(post.getId(), null);
        assertEquals(post.getId(), response.getId());
        assertEquals(null, response.getIsLiked());
    }

    @Test
    void shouldReturnPostWithLikedFalseWhenUserHasNotLiked() {
        var post = createTestPost();
        var uuid = UUID.randomUUID();
        var response = postUserService.getPost(post.getId(), uuid);
        assertEquals(post.getId(), response.getId());
        assertEquals(false, response.getIsLiked());
    }

    @Test
    void shouldReturnPostWithLikedTrueWhenUserHasLiked() {
        var post = createTestPost();
        var uuid = UUID.randomUUID();
        postLikeRepository.save(PostLike.create(uuid, post.getId()));
        var response = postUserService.getPost(post.getId(), uuid);
        assertEquals(post.getId(), response.getId());
        assertEquals(true, response.getIsLiked());
    }

    @Test
    void shouldReturnPostPreviewsWithLatestSearchType() {
        createTestPost();
        createTestPost();
        createTestPost();

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        var response = postUserService.search(eventFilter, null, 10);
        assertEquals(3, response.size());
        response.forEach(p -> assertEquals(null, p.score()));
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
            .searchString("Test")
            .sortField(SortField.SCORE)
            .build();
        var response = postUserService.search(eventFilter, null, 10);
        assertEquals(2, response.size());
        response.forEach(p -> assertNotNull(p.score()));
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

        var response = postUserService.search(eventFilter, uuid, 10);
        assertEquals(2, response.size());
        var likedPost = response.stream()
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
        documentVectorRepository.deleteAll();
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
    }
}