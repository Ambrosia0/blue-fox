package com.ambrosia.content_service.post.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.infrastructure.grpc.ProfileService;
import com.ambrosia.content_service.post.api.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.domain.repository.PostRepository;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.infrastructure.entity.PostElastic;
import com.ambrosia.content_service.util.CommunityCreator;
import com.ambrosia.content_service.util.FollowCreator;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.PostTemplate;
import com.ambrosia.content_service.util.UserCreator;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;

public class PostEditorIntegrationTests extends BaseIntegrationTest{
    @Autowired PostEditorService postEditorService;
    @Autowired PostRepository postRepository;
    @Autowired PostCreator postCreator;
    @Autowired FollowCreator followCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired UserCreator userCreator;
    @Autowired ElasticsearchOutboxRelay relay;
    @MockitoBean ProfileService profileService;

    @Autowired ElasticsearchOperations operations;

    @Test
    void shouldDeleteDraft(){
        var post = postCreator.create(false);
        assertTrue(postRepository.findById(post.getId()).isPresent());
        postEditorService.deleteDraftPost(
            post.getId(),
            Actor.builder()
                .id(post.getAuthorId())
                .role(Role.USER)
                .build()
        );
        assertFalse(postRepository.findById(post.getId()).isPresent());
    }

    @Test
    void shouldRepublishDraft(){
        var post = postCreator.create(true);
        var prevInstant = post.getPublishedAt();
        assertDoesNotThrow(
            () -> postEditorService.unpublishPost(
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build(), 
                post.getId()
            )
        );
        assertFalse(postRepository.findById(post.getId()).get().isPublished());
        relay.flush();
        operations.indexOps(PostElastic.class).refresh();
        assertFalse(operations.exists(post.getId().toString(), PostElastic.class));
        assertDoesNotThrow(
            () -> postEditorService.publishPost(
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build(),
                post.getId()
            )
        );

        relay.flush();
        operations.indexOps(PostElastic.class).refresh();
        assertTrue(operations.exists(post.getId().toString(), PostElastic.class));
        assertDoesNotThrow(() -> {
            var updated = postRepository.findById(post.getId()).get();
            var dif = Math.abs(
                ChronoUnit.MICROS.between(
                    prevInstant.truncatedTo(ChronoUnit.MICROS), 
                    updated.getPublishedAt().truncatedTo(ChronoUnit.MICROS)
                )
            );
            assertTrue(dif <= 2);
            assertTrue(updated.isPublished() && updated.isRepublished());
        });
    }

    @Test
    void shouldCreateUnpublishedPost(){
        var author = userCreator.create();
        var resp = postEditorService.createPost(
            Actor.builder()
                .id(author.getId())
                .role(Role.USER)
                .build(),
            PostCreateRequest.builder()
                .title("test title")
                .build()
            );
        var post = postRepository.findById(resp.id());
        assertTrue(post.isPresent());
        assertFalse(post.get().isPublished());
    }

    @Test
    void shouldThrowInvalidContentExceptionOnEdit(){
        var post = postCreator.create(false);
        assertThrows(
            InvalidContentException.class,
            () -> postEditorService.editPost(
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build(), 
                post.getId(),
                createEditRequest("random content")
            )
        );
    }

    @Test
    void shouldEditPost(){
        var post = postCreator.create(false);
        postEditorService.editPost(
            Actor.builder()
                .id(post.getAuthorId())
                .role(Role.USER)
                .build(), 
            post.getId(),
            createEditRequest(PostTemplate.template)
        );
    }

    @Test
    void shouldThrowInvalidPostVersionException(){
        var post = postCreator.create(false);
        assertDoesNotThrow(
            () -> postEditorService.editPost( 
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build(), 
                post.getId(),
                createEditRequest(PostTemplate.template)
            )
        );
        assertThrows(
            InvalidPostVersionException.class,
            () -> postEditorService.editPost(
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build(),
                post.getId(),
                createEditRequest(PostTemplate.template)
            )
        );
    }

    @Test
    void shouldReturnPostContentOnGetContent(){
        var post = postCreator.create(true);
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(
                post.getId(), 
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test
    void shouldPublishPost(){
        var post = postCreator.create(false);
            assertDoesNotThrow(() -> postEditorService.publishPost(
                Actor.builder()
                    .id(post.getAuthorId())
                    .role(Role.USER)
                    .build(),
                post.getId()
            )
        );
    }

    @Test
    void shouldReturnEmptyList(){
        var post = postCreator.create(true);
        assertTrue(postEditorService.getUnpublishedPosts(
            Actor.builder()
                .id(post.getAuthorId())
                .role(Role.USER)
                .build(), 
            PostEditorFilter.builder().build(),
            PageRequest.ofSize(10).first()).isEmpty()
        );
    }

    @Test
    void shouldReturnUnpublishedPost(){
        var post = postCreator.create(false);
        var posts = postEditorService.getUnpublishedPosts(
            Actor.builder()
                .id(post.getAuthorId())
                .role(Role.USER)
                .build(),
            PostEditorFilter.builder().build(),
            PageRequest.ofSize(10).first());
        assertFalse(posts.isEmpty());
        assertDoesNotThrow(() -> posts.getContent().getFirst());
    }

    @Test 
    void shouldCreatePostWithReply(){
        var originalPost = postCreator.create(true);

        var user = userCreator.create();
        assertDoesNotThrow(
            () -> {
                var resp = postEditorService.createPost(
                    Actor.builder()
                        .id(user.getId())
                        .role(Role.USER)
                        .build(),
                    PostCreateRequest.builder()
                        .replyId(originalPost.getId())
                        .title("TestTitle")
                        .build()
                );
                assertNotNull(postRepository.findById(resp.id()).get().getReplyId());
            }
        );
    }

    @AfterEach 
    void cleanUp(){
        postCreator.cleanUp();
        communityCreator.cleanUp();
        userCreator.cleanUp();
    }

    private PostEditRequest createEditRequest(String content){
        return new PostEditRequest("Test title", content, List.of("#testTag"), 0L);
    }
}
