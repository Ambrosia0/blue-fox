package com.ambrosia.comment_service.comment.application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

import com.ambrosia.comment_service.BaseIntegrationTest;
import com.ambrosia.comment_service.attachment.infrastructure.persistence.AttachmentRepository;
import com.ambrosia.comment_service.comment.domain.repository.CommentRepository;
import com.ambrosia.comment_service.community.model.entity.CommunityPermission;
import com.ambrosia.comment_service.like.infrastructure.persistence.JdbcLikeRepository;
import com.ambrosia.comment_service.utils.CommentCreator;
import com.ambrosia.comment_service.utils.CommunityCreator;
import com.ambrosia.comment_service.utils.CommentCreator.CommentContext;
import com.ambrosia.comment_service.utils.CommunityFollowCreator;
import com.ambrosia.comment_service.utils.PostProjectionCreator;
import com.ambrosia.comment_service.utils.UserCreator;
import com.ambrosia.comment_service.utils.factory.CommentRequestFactory;
import com.ambrosia.comment_service.utils.factory.FileMetadataFactory;
import com.ambrosia.community_service.kafka_events.Permission;
import com.ambrosia.library_policy.policy.Actor;
import com.ambrosia.library_policy.policy.Actor.Role;
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
    @Autowired JdbcLikeRepository likeRepository;

    @Autowired RestClient testRestClient;
    @Autowired AttachmentRepository attachmentRepository;

    @Test
    void shouldCreateRootCommentWithoutAttachment(){
        var user = userCreator.create();
        var post = postProjectionCreator.create();
        var request = CommentRequestFactory.createCommentRequest(post.getId());
        var resp = userCommentService.createComment(
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build(), 
            request
        );
        assertDoesNotThrow(() -> commentRepository.findById(resp.getId()).get());
    }

    @Test
    void shouldCreateTreeCommentWithoutAttachment(){
        var user = userCreator.create();
        var post = postProjectionCreator.create();
        var rootRequest = CommentRequestFactory.createCommentRequest(post.getId());
        var respRoot = userCommentService.createComment(
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build(), 
            rootRequest
        );
        var treeRequest = CommentRequestFactory.createCommentRequest(
            post.getId(), respRoot.getId(), null);
        var respTree = userCommentService.createComment(
            Actor.builder()
                .id(user.getId())
                .role(Role.USER)
                .build(), 
            treeRequest
        );
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

        var actor = Actor.builder()
            .id(user.getId())
            .role(Role.USER)
            .build();
        var resp = userCommentService.createComment(
            actor, 
            commentRequest
        );
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
                actor, 
                resp.getId(), 
                resp.getAttachmentUploadResponse().attachmentId()
            )
        );
        assertNotNull(attachmentRepository.findById(resp.getAttachmentUploadResponse().attachmentId()));
    }

    @Test 
    void shouldDeleteCommentWhenAuthorInRequest(){
        var comment = commentCreator.createWithPost();
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                comment.getId(), 
                Actor.builder()
                    .id(comment.getUserId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test 
    void shouldDeleteRelatedToCommunityCommentWhenUserIsAuthor(){
        var createResp = commentCreator.createWithPostAndCommunity(false);
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                Actor.builder()
                    .id(createResp.comment().getUserId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @Test 
    void shouldDeleteRelatedToPrivateCommunityCommentWhenUserIsAuthor(){
        var createResp = commentCreator.createWithPostAndCommunity(true);
        assertDoesNotThrow(
            () -> userCommentService.deleteComment(
                createResp.comment().getId(), 
                Actor.builder()
                    .id(createResp.comment().getUserId())
                    .role(Role.USER)
                    .build()
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
                Actor.builder()
                    .id(createResp.comment().getUserId())
                    .role(Role.USER)
                    .build()
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
                Actor.builder()
                    .id(createResp.comment().getUserId())
                    .role(Role.USER)
                    .build()
            )
        );
    }

    @AfterEach
    void cleanUp(){
        likeRepository.deleteAll();
    }
}
