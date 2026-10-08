package com.ambrosia.content_service.post.application;

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

import com.ambrosia.community_service.kafka_events.Permission;
import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.NotEnoughPermissionsException;
import com.ambrosia.content_service.like.infrastructure.persistence.JdbcPostLikeRepository;
import com.ambrosia.content_service.post.api.dto.EventFilter;
import com.ambrosia.content_service.post.api.dto.EventFilter.SearchType;
import com.ambrosia.content_service.post.api.dto.EventFilter.SortField;
import com.ambrosia.content_service.post.domain.repository.DocumentVectorRepository;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.search.infrastructure.PostIndexService;
import com.ambrosia.content_service.search.infrastructure.mappers.PostIndexMapper;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;

@Transactional
@ActiveProfiles(profiles = {"es-disabled"}, inheritProfiles = true)
public class PostUserServiceIntegrationTests extends BaseIntegrationTest {
    @Autowired PostUserService postUserService;

    @Autowired JdbcPostLikeRepository postLikeRepository;

    @Autowired DocumentVectorRepository documentVectorRepository;

    @Autowired PostIndexService postIndexService;

    @Autowired PostIndexMapper postIndexMapper;

    @Autowired PostCreator postCreator;

    @Autowired UserCreator userCreator;

    @Test
    void shouldThrowPostDoesntExistException() {
        assertThrows(
            PostDoesntExistException.class,
            () -> postUserService.getPost(
                999L, 
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build()
            )
        );
    }

    @Test
    void shouldReturnPostWithoutLikeWhenUserIsAnonymous() {
        var post = postCreator.create(true);
        var response = postUserService.getPost(
            post.getId(), 
            Actor.builder()
                .role(Role.ANONYMOUS)
                .build()
        );
        assertEquals(post.getId(), response.getId());
        assertEquals(null, response.getIsLiked());
    }

    @Test
    void shouldReturnPostWithLikedFalseWhenUserHasNotLiked() {
        var post = postCreator.create(true);
        var user = userCreator.create();
        var response = postUserService.getPost(
            post.getId(), 
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build()
        );
        assertEquals(post.getId(), response.getId());
        assertEquals(false, response.getIsLiked());
    }

    @Test
    void shouldReturnPostWithLikedTrueWhenUserHasLiked() {
        var post = postCreator.create(true);
        var user = userCreator.create();
        postLikeRepository.save(user.getId(), post.getId());
        var response = postUserService.getPost(
                post.getId(), 
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build()
        );
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

        var response = postUserService.search(
            eventFilter, 
            Actor.builder()
                .role(Role.ANONYMOUS)
                .build(), 
            10
        );
        assertEquals(3, response.size());
        response.forEach(p -> assertEquals(null, p.score()));
    }

    @Test
    void shouldReturnEmptyListWhenNoPostsFound() {
        var eventFilter = EventFilter.builder()
            .searchType(SearchType.POPULAR)
            .build();

        var response = postUserService.search(
                eventFilter, 
                Actor.builder()
                    .role(Role.ANONYMOUS)
                    .build(), 
                    10
                );
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
                Actor.builder()
                    .role(Role.USER)
                    .id(searchingUser.getId())
                    .build(),
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
        postLikeRepository.save(user.getId(), post1.getId());

        var eventFilter = EventFilter.builder()
            .searchType(SearchType.LATEST)
            .build();

        var response = postUserService.search(
                eventFilter,
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build(), 
                    10
        );
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
            () -> postUserService.deletePost(
                post.getId(), 
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test 
    void shouldThrowNotEnoughPermissionsOnPostDeleteWhenDeletingIsNotAuthor(){
        var post = postCreator.create(true);
        var user = userCreator.create();
        assertThrows(
            NotEnoughPermissionsException.class, 
            () -> postUserService.deletePost(
                post.getId(), 
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test 
    void shouldThrowPostDoesntExistOnPostDeleteWhenPostDoesntExist(){
        var user = userCreator.create();
        assertThrows(
            PostDoesntExistException.class,
            () -> postUserService.deletePost(
                ThreadLocalRandom.current().nextLong(),
                Actor.builder()
                    .id(user.getId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnDeletePostInPublicCommunityWhenUserIsModerator(){
        var createResp = postCreator.createPublishedWithCommunity(false);
        var moderatorId = createResp.communityProjection().getPermissions()
            .stream()
            .filter(p -> p.getPermission().equals(Permission.POST_DELETE.name()))
            .findFirst()
            .get()
            .getUserId();
        assertDoesNotThrow(
            () -> postUserService.deletePost(
                createResp.post().getId(), 
                Actor.builder()
                    .id(moderatorId)
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @AfterEach
    void cleanUp() {
        postLikeRepository.deleteAll();
    }
}