package com.ambrosia.content_service.integration.post;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import com.ambrosia.content_service.BaseIntegrationTest;
import com.ambrosia.content_service.exception.api.InvalidContentException;
import com.ambrosia.content_service.exception.api.InvalidPostVersionException;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.post.exception.PostDoesntExistException;
import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.entity.Post;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.UserActor;
import com.ambrosia.content_service.util.Factory;
import com.ambrosia.content_service.util.PostTemplate;

@Transactional
public class PostEditorIntegrationTests extends BaseIntegrationTest{
    @Autowired PostEditorService postEditorService;
    @Autowired PostRepository postRepository;
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
        var post = createUnpublishedPost();
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
            authorId, new PostCreateRequest("TestTitle", null));
        var post = postRepository.findById(resp.id());
        assertTrue(post.isPresent());
        assertFalse(post.get().isPublished());
    }

    @Test
    void shouldThrowPostDoesntExistExceptionOnEdit(){
        var post = createUnpublishedPost();
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
        var post = createUnpublishedPost();
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
        var post = createUnpublishedPost();
        postEditorService.editPost(
            post.getAuthorId(), 
            post.getId(),
            createEditRequest(PostTemplate.template)
        );
    }

    @Test
    void shouldThrowInvalidPostVersionException(){
        var post = createUnpublishedPost();
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
        var post = createPublishedPost();
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(post.getId(), post.getAuthorId()));
    }

    @Test
    void shouldReturnPostContentOnGetContent(){
        var post = createPublishedPost();
        assertThrows(
            PostDoesntExistException.class, 
            () -> postEditorService.getContent(post.getId(), post.getAuthorId()));
    }

    @Test
    void shouldThrowPostDoesntExistExceptionOnPublish(){
        var post = createPublishedPost();
        assertThrows(
            PostDoesntExistException.class,
            () -> postEditorService.publishPost(post.getAuthorId(), post.getId()));
    }

    @Test
    void shouldPublishPost(){
        var post = createUnpublishedPost();
        assertDoesNotThrow(() -> postEditorService.publishPost(post.getAuthorId(), post.getId()));
    }

    @Test
    void shouldReturnEmptyList(){
        var post = createPublishedPost();
        assertTrue(postEditorService.getUnpublishedPosts(
            post.getAuthorId(), 
            PageRequest.ofSize(10).first()).isEmpty()
        );
    }

    @Test
    void shouldReturnUnpublishedPost(){
        var post = createUnpublishedPost();
        var posts = postEditorService.getUnpublishedPosts(
            post.getAuthorId(),
            PageRequest.ofSize(10).first());
        assertFalse(posts.isEmpty());
        assertDoesNotThrow(() -> posts.getContent().getFirst());
    }

    private Post createUnpublishedPost(){
        var post = Factory.createTestPost();
        post.setPublished(false);
        post.setPublishedAt(null);
        return postRepository.save(post);
    }

    private Post createPublishedPost(){
        var post = Factory.createTestPost();
        post.setPublished(true);
        post.setPublishedAt(null);
        return postRepository.save(post);
    }

    private PostEditRequest createEditRequest(String content){
        return new PostEditRequest("Test title", content, List.of("#testTag"), 0L);
    }
}
