package com.ambrosia.content_service.integration.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.like.model.entity.PostLike;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.post.utils.policy.AnonymousActor;
import com.ambrosia.content_service.post.utils.policy.UserActor;
import com.ambrosia.content_service.search.model.dto.EventFilter;
import com.ambrosia.content_service.search.model.dto.SearchType;
import com.ambrosia.content_service.search.model.dto.EventFilter.SortField;
import com.ambrosia.content_service.search.repository.DocumentVectorRepository;
import com.ambrosia.content_service.search.service.PostIndexService;
import com.ambrosia.content_service.search.service.mappers.PostIndexMapper;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.UserCreator;

@Transactional
@ActiveProfiles(profiles = {"es-disabled"}, inheritProfiles = true)
public class PostUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired PostUserService postUserService;

    @Autowired PostLikeRepository postLikeRepository;

    @Autowired DocumentVectorRepository documentVectorRepository;

    @Autowired PostIndexService postIndexService;

    @Autowired PostIndexMapper postIndexMapper;

    @Autowired PostCreator postCreator;

    @Autowired UserCreator userCreator;

    @Test
    void shouldThrowPostDoesntExistException() {
        assertThrows(
            PostDoesntExistException.class,
            () -> postUserService.getPost(999L, new AnonymousActor())
        );
    }

    @Test
    void shouldReturnPostWithoutLikeWhenUserIsAnonymous() {
        var post = postCreator.create(true);
        var response = postUserService.getPost(post.getId(), new AnonymousActor());
        assertEquals(post.getId(), response.getId());
        assertEquals(null, response.getIsLiked());
    }

    @Test
    void shouldReturnPostWithLikedFalseWhenUserHasNotLiked() {
        var post = postCreator.create(true);
        var user = userCreator.create();
        var response = postUserService.getPost(post.getId(), new UserActor(user.getId()));
        assertEquals(post.getId(), response.getId());
        assertEquals(false, response.getIsLiked());
    }

    @Test
    void shouldReturnPostWithLikedTrueWhenUserHasLiked() {
        var post = postCreator.create(true);
        var user = userCreator.create();
        postLikeRepository.save(PostLike.create(user.getId(), post.getId()));
        var response = postUserService.getPost(post.getId(), new UserActor(user.getId()));
        assertEquals(post.getId(), response.getId());
        assertEquals(true, response.getIsLiked());
    }

    @Test
    void shouldReturnPostPreviewsWithLatestSearchType() {
        postCreator.create(true);
        postCreator.create(true);
        postCreator.create(true);

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        var response = postUserService.search(eventFilter, new AnonymousActor(), 10);
        assertEquals(3, response.size());
        response.forEach(p -> assertEquals(null, p.score()));
    }

    @Test
    void shouldReturnEmptyListWhenNoPostsFound() {
        var eventFilter = EventFilter.builder()
            .searchType(SearchType.POPULAR)
            .build();

        var response = postUserService.search(eventFilter, new AnonymousActor(), 10);
        assertEquals(0, response.size());
    }

    @Test
    void shouldReturnPostPreviewsWithScore() {
        postCreator.create(true);
        postCreator.create(true);

        var searchingUser = userCreator.create();
        var eventFilter = EventFilter.builder()
            .searchType(SearchType.RELEVANCY)
            .searchString("Test")
            .sortField(SortField.SCORE)
            .build();
        var response = postUserService.search(
                eventFilter, 
                new UserActor(searchingUser.getId()),
                10
            );
        assertEquals(2, response.size());
        response.forEach(p -> assertNotNull(p.score()));
    }

    @Test
    void shouldReturnPostPreviewsWithLikeStatus() {
        var post1 = postCreator.create(true);
        postCreator.create(true);

        var user = userCreator.create();
        postLikeRepository.save(PostLike.create(user.getId(), post1.getId()));

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        var response = postUserService.search(eventFilter, new UserActor(user.getId()), 10);
        assertEquals(2, response.size());
        var likedPost = response.stream()
            .filter(p -> p.postViewResponse().id() == post1.getId())
            .findFirst()
            .orElseThrow();
        assertEquals(true, likedPost.postViewResponse().isLiked());
    }

    @Test 
    void shouldNotThrowExceptionOnDeletePublishedPostWhenAuthorIsDeleting(){
        var post = postCreator.create(true);
        assertDoesNotThrow(
            () -> postUserService.deletePost(post.getId(), new UserActor(post.getAuthorId()))
        );
    }

    @Test 
    void shouldThrowNotEnoughPermissionsOnPostDeleteWhenDeletingIsNotAuthor(){
        var post = postCreator.create(true);
        var user = userCreator.create();
        assertThrows(
            NotEnoughPermissionsException.class, 
            () -> postUserService.deletePost(post.getId(), new UserActor(user.getId()))
        );
    }

    @Test 
    void shouldThrowPostDoesntExistOnPostDeleteWhenPostDoesntExist(){
        var user = userCreator.create();
        assertThrows(
            PostDoesntExistException.class,
            () -> postUserService.deletePost(
                ThreadLocalRandom.current().nextLong(),
                new UserActor(user.getId())
            )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnDeletePostInPublicCommunityWhenUserIsModerator(){
        var createResp = postCreator.createPublishedWithCommunity(false);
        var moderatorId = createResp.communityProjection().getPermissions()
            .stream()
            .findFirst()
            .get()
            .getUserId();
        assertDoesNotThrow(
            () -> postUserService.deletePost(
                createResp.post().getId(), 
                new UserActor(moderatorId)
            )
        );
    }

    @AfterEach
    void cleanUp() {
        postLikeRepository.deleteAll();
        postCreator.cleanUp();
    }
}