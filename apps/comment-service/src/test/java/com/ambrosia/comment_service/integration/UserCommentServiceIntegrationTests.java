package com.ambrosia.comment_service.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.nio.file.Files;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.client.RestClient;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.attachment.repository.AttachmentRepository;
import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.EventFilter.SortField;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.CommentQueryService;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.community.repository.CommunityProjectionRepository;
import com.ambrosia.comment_service.community.service.CommunityPermissionService;
import com.ambrosia.comment_service.exceptions.api.CommentOrPostDoesntExistException;
import com.ambrosia.comment_service.exceptions.api.PostDoesntExistException;
import com.ambrosia.comment_service.like.model.entity.CommentLike;
import com.ambrosia.comment_service.like.repository.LikeRepository;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;
import com.ambrosia.comment_service.utils.factory.CommentFactory;
import com.ambrosia.comment_service.utils.factory.CommentRequestFactory;
import com.ambrosia.comment_service.utils.factory.FileMetadataFactory;
import com.ambrosia.library_s3.TestS3Configuration;

@Import({
    TestS3Configuration.class
})
public class UserCommentServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired UserCommentService userCommentService;

    @Autowired PostProjectionCreator postProjectionCreator;
    @Autowired CommentCreator commentCreator;

    @Autowired CommentQueryService commentQueryService;
    @Autowired PostProjectionRepository postProjectionRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired LikeRepository likeRepository;
    @Autowired CommunityProjectionRepository communityProjectionRepository;

    @Autowired RestClient testRestClient;
    @Autowired AttachmentRepository attachmentRepository;

    @MockitoBean CommunityPermissionService communityPermissionService;

    @Test
    void shouldThrowPostDoesntExistException(){
        assertThrows(
            PostDoesntExistException.class,
            () -> userCommentService.createComment(
                UUID.randomUUID(), 
                CommentRequestFactory.createCommentRequest(ThreadLocalRandom.current().nextLong()))
        );
    }

    @Test
    void shouldThrowCommentOrPostDoesntExistExceptionOnTreeComment(){
        var userId = UUID.randomUUID();
        var post = postProjectionCreator.createFromScratch();
        var request = CommentRequestFactory.createCommentRequest(
            post.getId(), 
            ThreadLocalRandom.current().nextLong(),
            null
        );
        assertThrows(
            CommentOrPostDoesntExistException.class, 
            () -> userCommentService.createComment(userId, request));
    }

    @Test
    void shouldCreateRootCommentWithoutAttachment(){
        var uuid = UUID.randomUUID();
        var post = postProjectionCreator.createFromScratch();
        var request = CommentRequestFactory.createCommentRequest(post.getId());
        var resp = userCommentService.createComment(uuid, request);
        commentRepository.findById(resp.getId()).get();
        assertEquals(1, commentRepository.count());
    }

    @Test
    void shouldCreateTreeCommentWithoutAttachment(){
        var uuid = UUID.randomUUID();
        var post = postProjectionCreator.createFromScratch();
        var rootRequest = CommentRequestFactory.createCommentRequest(post.getId());
        var respRoot = userCommentService.createComment(uuid, rootRequest);
        var treeRequest = CommentRequestFactory.createCommentRequest(
            post.getId(), respRoot.getId(), null);
        var respTree = userCommentService.createComment(uuid, treeRequest);
        commentRepository.findById(respTree.getId()).get();
        assertEquals(2, commentRepository.count());
    }

    @Test
    void shouldCreateRootCommentWithAttachment() throws Exception{
        var post = postProjectionCreator.createFromScratch();
        var userId = UUID.randomUUID();
        var file = FileMetadataFactory.fileMetadata();
        var commentRequest = CommentRequestFactory.createCommentRequest(
            post.getId(), 
            null, 
            file
        );
        var resp = userCommentService.createComment(userId, commentRequest);
        assertNotNull(resp.getAttachmentUploadResponse());
        assertNotNull(resp.getAttachmentUploadResponse().attachmentId());
        testRestClient
            .put()
            .uri(URI.create(resp.getAttachmentUploadResponse().uploadUrl()))
            .header("x-amz-checksum-md5", file.md5())
            .contentType(MediaType.parseMediaType(file.contentType().getMimeType()))
            .contentLength(file.fileSize())
            .body(Files.readAllBytes(FileMetadataFactory.testImagePath))
            .retrieve()
            .toBodilessEntity();
        assertDoesNotThrow(
            () -> userCommentService.confirmAttachmentUpload(
                userId, 
                resp.getId(), 
                resp.getAttachmentUploadResponse().attachmentId()
            )
        );
        assertNotNull(attachmentRepository.findById(resp.getAttachmentUploadResponse().attachmentId()));
    }

    @Test
    void shouldReturnRootCommentsWithoutLike(){
        var post = postProjectionCreator.createFromScratch();
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        var resp = commentQueryService.getCommentsForPost(post.getId(), 
            EventFilter.builder().sortField(SortField.HOT).build(), 
            null);
        assertEquals(0, resp.stream().filter(c -> c.isLiked() != null).count());
    }

    @Test
    void shouldReturnRootCommentsWithLike(){
        var post = postProjectionCreator.createFromScratch();
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        var likedComm = commentCreator.create(post.getId());
        var uuid = UUID.randomUUID();
        likeRepository.save(CommentLike.create(likedComm.getId(), uuid));
        var resp = commentQueryService.getCommentsForPost(
            post.getId(), 
            EventFilter.builder().sortField(SortField.HOT).build(), 
            uuid);
        assertEquals(1, resp.stream().filter(c -> c.isLiked() != null && c.isLiked()).count());
    }

    @Test
    void shouldReturnTreeCommentsWithoutLike(){
        var post = postProjectionCreator.createFromScratch();
        var root = commentCreator.create(post.getId());
        commentCreator.create(post.getId(), root.getId());
        commentCreator.create(post.getId(), root.getId());
        var resp = commentQueryService.getCommentTree(root.getId(), null);
        assertEquals(2, resp.size());
        assertEquals(0, resp.stream().filter(c -> c.isLiked() != null).count());
    }

    @Test
    void shouldReturnTreeCommentsWithLike(){
        var post = postProjectionCreator.createFromScratch();
        var root = commentRepository.save(CommentFactory.create(post.getId(), null));
        commentCreator.create(post.getId(), root.getId());
        commentCreator.create(post.getId(), root.getId());
        var likedComm = commentCreator.create(post.getId(), root.getId());;
        var uuid = UUID.randomUUID();
        likeRepository.save(CommentLike.create(likedComm.getId(), uuid));
        var resp = commentQueryService.getCommentTree(root.getId(), uuid);
        assertEquals(3, resp.size());
        assertEquals(1, resp.stream().filter(c -> c.isLiked() != null && c.isLiked()).count());
    }

    @AfterEach
    void cleanUp(){
        likeRepository.deleteAll();
        commentRepository.deleteAll();
    }
}
