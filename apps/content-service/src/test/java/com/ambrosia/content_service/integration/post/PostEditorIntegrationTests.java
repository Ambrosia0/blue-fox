package com.ambrosia.content_service.integration.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.exception.api.PrivateReplyException;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.UserActor;
import com.ambrosia.content_service.util.CommunityCreator;
import com.ambrosia.content_service.util.FollowCreator;
import com.ambrosia.content_service.util.PostCreator;
import com.ambrosia.content_service.util.PostTemplate;

@Import({PostCreator.class, FollowCreator.class, CommunityCreator.class})
@Transactional
public class PostEditorIntegrationTests extends BaseIntegrationTest{
    @Autowired PostEditorService postEditorService;
    @Autowired PostRepository postRepository;
    @Autowired PostCreator postCreator;
    @Autowired FollowCreator followCreator;
    @Autowired CommunityCreator communityCreator;
    @MockitoBean ProfileService profileService;

    @Test
    void shouldThrowPostDoesntExistExceptionOnDelete(){
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.deletePost(
                ThreadLocalRandom.current().nextLong(), 
                new UserActor(UUID.randomUUID()))
        );
    }

    @Test
    void shouldDeletePost(){
        var post = postCreator.createUnpublished();
        assertTrue(postRepository.findById(post.getId()).isPresent());
        postEditorService.deletePost(
            post.getId(),
            new UserActor(post.getAuthorId())
        );
        assertFalse(postRepository.findById(post.getId()).isPresent());
    }

    @Test
    void shouldCreateUnpublishedPost(){
        var authorId = UUID.randomUUID();
        var resp = postEditorService.createPost(
            authorId, 
            new UserActor(authorId),
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
        var post = postCreator.createUnpublished();
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
        var post = postCreator.createUnpublished();
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
        var post = postCreator.createUnpublished();
        postEditorService.editPost(
            post.getAuthorId(), 
            post.getId(),
            createEditRequest(PostTemplate.template)
        );
    }

    @Test
    void shouldThrowInvalidPostVersionException(){
        var post = postCreator.createUnpublished();
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
        var post = postCreator.createPublished();
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(post.getId(), post.getAuthorId()));
    }

    @Test
    void shouldReturnPostContentOnGetContent(){
        var post = postCreator.createPublished();
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(post.getId(), post.getAuthorId()));
    }

    @Test
    void shouldThrowPostDoesntExistExceptionOnPublish(){
        var post = postCreator.createPublished();
        assertThrows(
            PostDoesntExistException.class,
            () -> postEditorService.publishPost(
                post.getAuthorId(),
                new UserActor(post.getAuthorId()),
                post.getId()
            )
        );
    }

    @Test
    void shouldPublishPost(){
        var post = postCreator.createUnpublished();
            assertDoesNotThrow(() -> postEditorService.publishPost(
                post.getAuthorId(),
                new UserActor(post.getAuthorId()),
                post.getId()
            )
        );
    }

    @Test
    void shouldReturnEmptyList(){
        var post = postCreator.createPublished();
        assertTrue(postEditorService.getUnpublishedPosts(
            post.getAuthorId(), 
            PageRequest.ofSize(10).first()).isEmpty()
        );
    }

    @Test
    void shouldReturnUnpublishedPost(){
        var post = postCreator.createUnpublished();
        var posts = postEditorService.getUnpublishedPosts(
            post.getAuthorId(),
            PageRequest.ofSize(10).first());
        assertFalse(posts.isEmpty());
        assertDoesNotThrow(() -> posts.getContent().getFirst());
    }

    @Test 
    void shouldCreatePostWithReply(){
        var originalPost = postCreator.createPublished();

        var userId = UUID.randomUUID();
        assertDoesNotThrow(
            () -> {
                var resp = postEditorService.createPost(
                    userId, 
                    new UserActor(userId),
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
        var originalPost = postCreator.createPublishedWithPrivateCommunity();

        var userId = UUID.randomUUID();
        assertThrows(
            PrivateReplyException.class,
            () -> postEditorService.createPost(
                    userId, 
                    new UserActor(userId),
                    PostCreateRequest.builder()
                        .replyId(originalPost.getId())
                        .title("TestTitle")
                        .build()
                )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnReplyToPrivateCommunityPostWithCommunity(){
        var originalPost = postCreator.createPublishedWithPrivateCommunity();

        var userId = UUID.randomUUID();
        followCreator.createFollow(
            originalPost.getCommunityId().getId(), 
            userId
        );
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                    userId, 
                    new UserActor(userId),
                    PostCreateRequest.builder()
                        .replyId(originalPost.getId())
                        .communityId(originalPost.getCommunityId().getId())
                        .title("TestTitle")
                        .build()
                )
        );
    }

    @Test 
    void shouldNotThrowDoesntFollowedExceptionOnReplyToPrivateCommunityPostWithCommunity(){
        var originalPost = postCreator.createPublishedWithPrivateCommunity();

        var userId = UUID.randomUUID();
        followCreator.createFollow(originalPost.getCommunityId().getId(), userId);
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                    userId, 
                    new UserActor(userId),
                    PostCreateRequest.builder()
                        .replyId(originalPost.getId())
                        .communityId(originalPost.getCommunityId().getId())
                        .title("TestTitle")
                        .build()
                )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnReplyToPublicCommunityPostWithoutCommunity(){
        var originalPost = postCreator.createPublishedWithPublicCommunity();
        var userId = UUID.randomUUID();
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                userId, 
                new UserActor(userId),
                PostCreateRequest.builder()
                    .replyId(originalPost.getId())
                    .title("TestTitle")
                    .build()
            )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnReplyToPublicCommunityPostInOtherCommunity(){
        var originalPost = postCreator.createPublishedWithPublicCommunity();
        var userId = UUID.randomUUID();
        var community = communityCreator.createPublic();
        followCreator.createFollow(community.getId(), userId);
        assertDoesNotThrow(
            () -> postEditorService.createPost(
                userId, 
                new UserActor(userId),
                PostCreateRequest.builder()
                    .replyId(originalPost.getId())
                    .title("TestTitle")
                    .communityId(community.getId())
                    .build()
            )
        );
    }

    @Test 
    void shouldThrowPrivateReplyExceptionOnReplyToPrivateCommunityPostInOtherPublicCommunity(){
        var originalPost = postCreator.createPublishedWithPrivateCommunity();
        var userId = UUID.randomUUID();
        var community = communityCreator.createPublic();
        followCreator.createFollow(community.getId(), userId);
        assertThrows(
            PrivateReplyException.class,
            () -> postEditorService.createPost(
                userId, 
                new UserActor(userId),
                PostCreateRequest.builder()
                    .replyId(originalPost.getId())
                    .title("TestTitle")
                    .communityId(community.getId())
                    .build()
            )
        );
    }

    private PostEditRequest createEditRequest(String content){
        return new PostEditRequest("Test title", content, List.of("#testTag"), 0L);
    }
}
