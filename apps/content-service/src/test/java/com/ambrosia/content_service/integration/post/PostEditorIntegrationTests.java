package com.ambrosia.content_service.integration.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.UserActor;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.util.CommunityCreator;
import com.ambrosia.content_service.util.FollowCreator;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.PostTemplate;
import com.ambrosia.content_service.util.UserCreator;
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
    void shouldThrowPostDoesntExistExceptionOnDeleteDraft(){
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.deleteDraftPost(
                ThreadLocalRandom.current().nextLong(), 
                new UserActor(UUID.randomUUID()))
        );
    }

    @Test
    void shouldDeleteDraft(){
        var post = postCreator.create(false);
        assertTrue(postRepository.findById(post.getId()).isPresent());
        postEditorService.deleteDraftPost(
            post.getId(),
            new UserActor(post.getAuthorId())
        );
        assertFalse(postRepository.findById(post.getId()).isPresent());
    }

    @Test
    void shouldRepublishDraft(){
        var post = postCreator.create(true);
        var prevInstant = post.getPublishedAt();
        assertDoesNotThrow(() -> postEditorService.unpublishPost(post.getAuthorId(), post.getId()));
        assertFalse(postRepository.findById(post.getId()).get().isPublished());
        relay.flush();
        operations.indexOps(PostElastic.class).refresh();
        assertFalse(operations.exists(post.getId().toString(), PostElastic.class));
        assertDoesNotThrow(
            () -> postEditorService.publishPost(
                new UserActor(post.getAuthorId()),
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
            author.getId(), 
            new UserActor(author.getId()),
            PostCreateRequest.builder()
                .title("test title")
                .build()
            );
        var post = postRepository.findById(resp.id());
        assertTrue(post.isPresent());
        assertFalse(post.get().isPublished());
    }

    @Test
    void shouldThrowPostDoesntExistExceptionOnEdit(){
        var post = postCreator.create(false);
        assertThrows(
            PostDoesntExistException.class,
            () -> postEditorService.editPost(
                UUID.randomUUID(), 
                post.getId(),
                createEditRequest(PostTemplate.template)
            )
        );
    }

    @Test
    void shouldThrowInvalidContentExceptionOnEdit(){
        var post = postCreator.create(false);
        assertThrows(
            InvalidContentException.class,
            () -> postEditorService.editPost(
                post.getAuthorId(), 
                post.getId(),
                createEditRequest("random content")
            )
        );
    }

    @Test
    void shouldEditPost(){
        var post = postCreator.create(false);
        postEditorService.editPost(
            post.getAuthorId(), 
            post.getId(),
            createEditRequest(PostTemplate.template)
        );
    }

    @Test
    void shouldThrowInvalidPostVersionException(){
        var post = postCreator.create(false);
        assertDoesNotThrow(
            () -> postEditorService.editPost( 
                post.getAuthorId(), 
                post.getId(),
                createEditRequest(PostTemplate.template)
            )
        );
        assertThrows(
            InvalidPostVersionException.class,
            () -> postEditorService.editPost(
                post.getAuthorId(),
                post.getId(),
                createEditRequest(PostTemplate.template)
            )
        );
    }

    @Test
    void shouldThrowPostDoesntExistExceptionOnGetContent(){
        var post = postCreator.create(true);
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(post.getId(), post.getAuthorId()));
    }

    @Test
    void shouldReturnPostContentOnGetContent(){
        var post = postCreator.create(true);
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(post.getId(), post.getAuthorId()));
    }

    @Test
    void shouldThrowPostDoesntExistExceptionOnPublish(){
        var post = postCreator.create(true);
        assertThrows(
            PostDoesntExistException.class,
            () -> postEditorService.publishPost(
                new UserActor(post.getAuthorId()),
                post.getId()
            )
        );
    }

    @Test
    void shouldPublishPost(){
        var post = postCreator.create(false);
            assertDoesNotThrow(() -> postEditorService.publishPost(
                new UserActor(post.getAuthorId()),
                post.getId()
            )
        );
    }

    @Test
    void shouldReturnEmptyList(){
        var post = postCreator.create(true);
        assertTrue(postEditorService.getUnpublishedPosts(
            post.getAuthorId(), 
            PostEditorFilter.builder().build(),
            PageRequest.ofSize(10).first()).isEmpty()
        );
    }

    @Test
    void shouldReturnUnpublishedPost(){
        var post = postCreator.create(false);
        var posts = postEditorService.getUnpublishedPosts(
            post.getAuthorId(),
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
                    user.getId(), 
                    new UserActor(user.getId()),
                    PostCreateRequest.builder()
                        .replyId(originalPost.getId())
                        .title("TestTitle")
                        .build()
                );
                assertNotNull(postRepository.findById(resp.id()).get().getReplyId());
            }
        );
    }

    @Test 
    void shouldThrowPrivateReplyExceptionOnReplyToPrivateCommunityPostWithoutCommunity(){
        var creatResp = postCreator.createPublishedWithCommunity(true);

        var user = userCreator.create();
        assertThrows(
            PrivateReplyException.class,
            () -> postEditorService.createPost(
                    user.getId(), 
                    new UserActor(user.getId()),
                    PostCreateRequest.builder()
                        .replyId(creatResp.post().getId())
                        .title("TestTitle")
                        .build()
                )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnReplyToPrivateCommunityPostWithCommunity(){
        var createResp = postCreator.createPublishedWithCommunity(true);

        var user = userCreator.create();
        followCreator.createFollow(
            createResp.post().getCommunityId().getId(), 
            user.getId()
        );
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                    user.getId(), 
                    new UserActor(user.getId()),
                    PostCreateRequest.builder()
                        .replyId(createResp.post().getId())
                        .communityId(createResp.post().getCommunityId().getId())
                        .title("TestTitle")
                        .build()
                )
        );
    }

    @Test 
    void shouldNotThrowDoesntFollowedExceptionOnReplyToPrivateCommunityPostWithCommunity(){
        var createResp = postCreator.createPublishedWithCommunity(true);

        var user = userCreator.create();
        followCreator.createFollow(createResp.post().getCommunityId().getId(), user.getId());
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                    user.getId(), 
                    new UserActor(user.getId()),
                    PostCreateRequest.builder()
                        .replyId(createResp.post().getId())
                        .communityId(createResp.post().getCommunityId().getId())
                        .title("TestTitle")
                        .build()
                )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnReplyToPublicCommunityPostWithoutCommunity(){
        var createResp = postCreator.createPublishedWithCommunity(false);
        var user = userCreator.create();
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                user.getId(), 
                new UserActor(user.getId()),
                PostCreateRequest.builder()
                    .replyId(createResp.post().getId())
                    .title("TestTitle")
                    .build()
            )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnReplyToPublicCommunityPostInOtherCommunity(){
        var createResp = postCreator.createPublishedWithCommunity(false);
        var user = userCreator.create();
        var community = communityCreator.create(false);
        followCreator.createFollow(community.getId(), user.getId());
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                user.getId(), 
                new UserActor(user.getId()),
                PostCreateRequest.builder()
                    .replyId(createResp.post().getId())
                    .title("TestTitle")
                    .communityId(community.getId())
                    .build()
            )
        );
    }

    @Test 
    void shouldThrowPrivateReplyExceptionOnReplyToPrivateCommunityPostInOtherPublicCommunity(){
        var createResp = postCreator.createPublishedWithCommunity(true);
        var user = userCreator.create();
        var community = communityCreator.create(false);
        followCreator.createFollow(community.getId(), user.getId());
        assertThrows(
            PrivateReplyException.class,
            () -> postEditorService.createPost(
                user.getId(), 
                new UserActor(user.getId()),
                PostCreateRequest.builder()
                    .replyId(createResp.post().getId())
                    .title("TestTitle")
                    .communityId(community.getId())
                    .build()
            )
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
