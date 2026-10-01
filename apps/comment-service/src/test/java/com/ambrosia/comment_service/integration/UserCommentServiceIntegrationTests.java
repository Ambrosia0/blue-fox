package com.ambrosia.comment_service.integration;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.attachment.repository.AttachmentRepository;
import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.model.dto.EventFilter.SortField;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.community.model.entity.CommunityPermission;
import com.ambrosia.comment_service.core.policy.AnonymousActor;
import com.ambrosia.comment_service.core.policy.UserActor;
import com.ambrosia.comment_service.exceptions.api.CommentOrPostDoesntExistException;
import com.ambrosia.comment_service.exceptions.api.NotEnoughPermissionsException;
import com.ambrosia.comment_service.exceptions.api.PostDoesntExistException;
import com.ambrosia.comment_service.like.model.entity.CommentLike;
import com.ambrosia.comment_service.like.repository.LikeRepository;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.CommunityCreator;
import com.ambrosia.comment_service.utils.CommentCreator.CommentContext;
import com.ambrosia.comment_service.utils.CommunityFollowCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;
import com.ambrosia.comment_service.utils.UserCreator;
import com.ambrosia.comment_service.utils.factory.CommentRequestFactory;
import com.ambrosia.comment_service.utils.factory.FileMetadataFactory;
import com.ambrosia.community_service.kafka_events.Permission;
import com.ambrosia.library_s3.TestS3Configuration;

@Import({
    TestS3Configuration.class
})
public class UserCommentServiceIntegrationTests extends BaseIntegrationTest{
    @Autowired UserCommentService userCommentService;

    @Autowired PostProjectionCreator postProjectionCreator;

    @Autowired CommentCreator commentCreator;
    @Autowired UserCreator userCreator;
    @Autowired CommunityCreator communityCreator;
    @Autowired CommunityFollowCreator followCreator;

    @Autowired CommentRepository commentRepository;
    @Autowired LikeRepository likeRepository;

    @Autowired RestClient testRestClient;
    @Autowired AttachmentRepository attachmentRepository;

    @Test
    void shouldThrowPostDoesntExistException(){
        var user = userCreator.create(); 
        assertThrows(
            PostDoesntExistException.class,
            () -> userCommentService.createComment(
                new UserActor(user.getId()), 
                CommentRequestFactory.createCommentRequest(ThreadLocalRandom.current().nextLong()))
        );
    }

    @Test
    void shouldThrowCommentOrPostDoesntExistExceptionOnTreeComment(){
        var user = userCreator.create();
        var post = postProjectionCreator.create();
        var request = CommentRequestFactory.createCommentRequest(
            post.getId(), 
            ThreadLocalRandom.current().nextLong(),
            null
        );
        assertThrows(
            CommentOrPostDoesntExistException.class, 
            () -> userCommentService.createComment(
                new UserActor(user.getId()), 
                request
            )
        );
    }

    @Test
    void shouldCreateRootCommentWithoutAttachment(){
        var user = userCreator.create();
        var post = postProjectionCreator.create();
        var request = CommentRequestFactory.createCommentRequest(post.getId());
        var resp = userCommentService.createComment(
            new UserActor(user.getId()), 
            request
        );
        assertDoesNotThrow(() -> commentRepository.findById(resp.getId()).get());
    }

    @Test
    void shouldCreateTreeCommentWithoutAttachment(){
        var user = userCreator.create();
        var post = postProjectionCreator.create();
        var rootRequest = CommentRequestFactory.createCommentRequest(post.getId());
        var respRoot = userCommentService.createComment(new UserActor(user.getId()), rootRequest);
        var treeRequest = CommentRequestFactory.createCommentRequest(
            post.getId(), respRoot.getId(), null);
        var respTree = userCommentService.createComment(new UserActor(user.getId()), treeRequest);
        commentRepository.findById(respTree.getId()).get();
        assertDoesNotThrow(() -> {
            commentRepository.findById(respRoot.getId()).get();
            commentRepository.findById(respTree.getId()).get();
        });
    }

    @Test
    void shouldCreateRootCommentWithAttachment() throws Exception{
        var post = postProjectionCreator.create();
        var user = userCreator.create();
        var file = FileMetadataFactory.fileMetadata();
        var commentRequest = CommentRequestFactory.createCommentRequest(
            post.getId(), 
            null, 
            file
        );
        var resp = userCommentService.createComment(new UserActor(user.getId()), commentRequest);
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
                new UserActor(user.getId()), 
                resp.getId(), 
                resp.getAttachmentUploadResponse().attachmentId()
            )
        );
        assertNotNull(attachmentRepository.findById(resp.getAttachmentUploadResponse().attachmentId()));
    }

    @Test
    void shouldReturnRootCommentsWithoutLike(){
        var post = postProjectionCreator.create();
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        var resp = userCommentService.getCommentsForPost(post.getId(), 
            EventFilter.builder().sortField(SortField.HOT).build(), 
            new AnonymousActor()
        );
        assertEquals(0, resp.stream().filter(c -> c.commentData().isLiked() != false).count());
    }

    @Test
    void shouldReturnRootCommentsWithLike(){
        var post = postProjectionCreator.create();
        commentCreator.create(post.getId());
        commentCreator.create(post.getId());
        var likedComm = commentCreator.create(post.getId());
        var user = userCreator.create();
        likeRepository.save(CommentLike.create(likedComm.getId(), user.getId()));
        var resp = userCommentService.getCommentsForPost(
            post.getId(), 
            EventFilter.builder().sortField(SortField.HOT).build(), 
            new UserActor(user.getId())
        );
        assertEquals(
            1, 
            resp.stream()
                .filter(c -> c.commentData().isLiked() != false && c.commentData().isLiked())
                .count()
        );
    }

    @Test
    void shouldReturnTreeCommentsWithoutLike(){
        var post = postProjectionCreator.create();
        var root = commentCreator.create(post.getId());

        var searched = List.of(
            commentCreator.create(post.getId(), root.getId()).getId(),
            commentCreator.create(post.getId(), root.getId()).getId()
        );

        var resp = userCommentService.getCommentTree(
            root.getId(),
            new AnonymousActor()
        );
        assertEquals(
            searched.size(), 
            resp.stream()
                .filter(t -> searched.contains(t.commentData().id()))
                .count()
        );
        assertEquals(0, resp.stream()
            .filter(c -> c.commentData().isLiked() == true)
            .count()
        );
    }

    @Test
    void shouldReturnTreeCommentsWithLike(){
        var post = postProjectionCreator.create();
        var root = commentCreator.create(post.getId());
        commentCreator.create(post.getId(), root.getId());
        commentCreator.create(post.getId(), root.getId());
        var likedComm = commentCreator.create(post.getId(), root.getId());
        var user = userCreator.create();
        likeRepository.save(CommentLike.create(likedComm.getId(), user.getId()));
        var resp = userCommentService.getCommentTree(root.getId(), new UserActor(user.getId()));
        assertEquals(3, resp.size());
        assertEquals(1, resp.stream().filter(c -> c.commentData().isLiked() != null && c.commentData().isLiked()).count());
    }

    @Test 
    void shouldDeleteCommentWhenAuthorInRequest(){
        var comment = commentCreator.createWithPost();
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(comment.getId(), new UserActor(comment.getUserId()))
        );
    }

    @Test 
    void shouldThrowNotEnoughPermissionsExceptionOnCommentDeleteWhenUserIsNotAuthor(){
        var comment = commentCreator.createWithPost();
        var user = userCreator.create();
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> userCommentService.deleteComment(comment.getId(), new UserActor(user.getId()))
        );
    }

    @Test 
    void shouldDeleteRelatedToCommunityCommentWhenUserIsAuthor(){
        var createResp = commentCreator.createWithPostAndCommunity(false);
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                new UserActor(createResp.comment().getUserId())
            )
        );
    }

    @Test 
    void shouldDeleteRelatedToPrivateCommunityCommentWhenUserIsAuthor(){
        var createResp = commentCreator.createWithPostAndCommunity(true);
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                new UserActor(createResp.comment().getUserId())
            )
        );
    }

    @Test 
    void shouldThrowNotEnoughPermissionsExceptionOnCommentDeleteInPrivateCommunityWhenUserIsNotAuthor(){
        CommentContext createResp = commentCreator.createWithPostAndCommunity(true);
        var user = userCreator.create();
        followCreator.create(
            createResp.postAndCommunity().communityProjection().getId(), 
            user.getId()
        );
        
        assertThrows(
            NotEnoughPermissionsException.class,
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                new UserActor(user.getId())
            )
        );
    }

    
    @Test 
    void shouldNotThrowExceptionOnCommentDeleteInPrivateCommunityWhenUserIsModerator(){
        CommentContext createResp = commentCreator.createWithPostAndCommunity(true);
        var user = userCreator.create();
        followCreator.create(
            createResp.postAndCommunity().communityProjection().getId(), 
            user.getId()
        );
        communityCreator.addPermission(
            createResp.postAndCommunity().communityProjection(),
            new CommunityPermission(Permission.COMMENT_DELETE.name(), user.getId())
        );
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                new UserActor(createResp.comment().getUserId())
            )
        );
    }

    @Test 
    void shouldNotThrowExceptionOnCommentDeleteInPublicCommunityWhenUserIsModerator(){
        CommentContext createResp = commentCreator.createWithPostAndCommunity(false);
        var user = userCreator.create();
        followCreator.create(
            createResp.postAndCommunity().communityProjection().getId(), 
            user.getId()
        );
        communityCreator.addPermission(
            createResp.postAndCommunity().communityProjection(),
            new CommunityPermission(Permission.COMMENT_DELETE.name(), user.getId())
        );
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                new UserActor(createResp.comment().getUserId())
            )
        );
    }

    @AfterEach
    void cleanUp(){
        likeRepository.deleteAll();
    }
}
