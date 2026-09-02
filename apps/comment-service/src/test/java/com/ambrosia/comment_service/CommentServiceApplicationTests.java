package com.ambrosia.comment_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.net.URI;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;

import com.ambrosia.comment_service.comment.model.dto.request.CreateComment;
import com.ambrosia.comment_service.comment.model.dto.response.CreateCommentResponse;
import com.ambrosia.comment_service.comment.repository.CommentRepository;
import com.ambrosia.comment_service.comment.service.UserCommentLikeService;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.like.repository.LikeRepository;
import com.ambrosia.comment_service.like.service.LikeAggregationService;
import com.ambrosia.comment_service.post.repository.PostProjectionRepository;
import com.ambrosia.comment_service.post.service.PostProjectionCreator;
import com.ambrosia.comment_service.utils.factory.CommentRequestFactory;
import com.ambrosia.comment_service.utils.factory.FileMetadataFactory;
import com.ambrosia.library_s3.TestS3Configuration;

import tools.jackson.databind.ObjectMapper;

@Import({
    TestS3Configuration.class
})
public class CommentServiceApplicationTests extends BaseIntegrationTest{

    @Autowired MockMvc mockMvc;

    @Autowired PostProjectionCreator projectionCreator;

    @Autowired ObjectMapper objectMapper;

    @Autowired UserCommentService userCommentService;

    @Autowired UserCommentLikeService userCommentLikeService;

    @Autowired LikeRepository likeRepository;

    @Autowired PostProjectionRepository projectionRepository;

    @Autowired LikeAggregationService likeAggregationService;

    @Autowired CommentRepository commentRepository;

    @Autowired RestClient testRestClient;

    @Autowired com.ambrosia.comment_service.utils.PostProjectionCreator postProjectionCreator;
    
    public static final Jwt jwt = Jwt.withTokenValue("token")
        .subject(UUID.randomUUID().toString())
        .claim("realm_access", Map.of("roles", List.of("user")))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(2048))
        .header("test", "testHEader")
        .build();
    
    @WithMockUser(roles = "USER")
    @Test
    void attachmentlessCommentCreationTest() throws Exception{
        var projection = postProjectionCreator.createFromScratch();

        mockMvc.perform(
            post("/api/user/comment")
            .with(jwt().jwt(t -> t.subject("11111111-1111-1111-1111-111111111111")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(CommentRequestFactory.createCommentRequest(projection.getId())))
        ).andExpect(status().isCreated());
    }

    @WithMockUser(roles = "USER")
    @Test
    void attachmentCommentCreationTest() throws Exception{
        var file = FileMetadataFactory.fileMetadata();
        var projection = postProjectionCreator.createFromScratch();
        var resp = mockMvc.perform(
            post("/api/user/comment")
            .with(jwt().jwt(t -> t.subject("11111111-1111-1111-1111-111111111111")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(
                CommentRequestFactory.createCommentRequest(
                    projection.getId(),
                    null,
                    file
                )))
        )
        .andExpect(status().isCreated())
        .andReturn()
        .getResponse()
        .getContentAsString();
        var attachResp = objectMapper.readValue(resp, CreateCommentResponse.class);
        if(attachResp.getAttachmentUploadResponse() == null)
            throw new RuntimeException("Doesn't contain attachment upload url");
        testRestClient
            .put()
            .uri(URI.create(attachResp.getAttachmentUploadResponse().uploadUrl()))
            .header("x-amz-checksum-md5", file.md5())
            .contentType(MediaType.parseMediaType(file.contentType().getMimeType()))
            .contentLength(file.fileSize())
            .body(Files.readAllBytes(FileMetadataFactory.testImagePath))
            .retrieve()
            .toBodilessEntity();
        var confirmResp = mockMvc.perform(
            post(
                "/api/user/comment/{commentId}/attachment/{attachmentId}", 
                attachResp.getId(), 
                attachResp.getAttachmentUploadResponse().attachmentId()
            )
            .with(jwt().jwt(t -> t.subject("11111111-1111-1111-1111-111111111111")))
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        var confirmBody = objectMapper.readValue(confirmResp, CreateCommentResponse.class);
        assertNotNull(confirmBody.getAttachmentId());
    }

    @Test
    void commentGetTest() throws Exception{
        var proj = postProjectionCreator.createFromScratch();
        var res = mockMvc.perform(
            post("/api/user/comment")
            .with(jwt().jwt(t -> t.subject("11111111-1111-1111-1111-111111111111")))
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                objectMapper.writeValueAsString(
                    new CreateComment(proj.getId(), "test", null, null)
                )
            )
        )
            .andExpect(status().isCreated())
            .andReturn()
            .getResponse()
            .getContentAsString();
        var resObj = objectMapper.readValue(res, CreateCommentResponse.class);
        mockMvc.perform(get("/api/public/comment/"+resObj.getId()))
            .andExpect(status().isOk());
    }

    @Test
    void commentGetTestUnauthorized() throws Exception {
        mockMvc.perform(get("/api/public/post/1/comments")
            .param("lastSeenId", "2")
        )
        .andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void commentGetTestAuthorized() throws Exception {
        mockMvc.perform(get("/api/public/post/1/comments")
            .with(jwt().jwt(t -> t.subject("11111111-1111-1111-1111-111111111111")))
            .param("lastSeenId", "2")
        )
        .andExpect(status().isOk());
    }

    @Test
    void likeAggregationTest(){
        var projection = postProjectionCreator.createFromScratch();
        var first = userCommentService.createComment(
            UUID.randomUUID(), CommentRequestFactory.createCommentRequest(projection.getId()));
        var second = userCommentService.createComment(
            UUID.randomUUID(), CommentRequestFactory.createCommentRequest(projection.getId()));
        var deletable = UUID.randomUUID();
        var id = UUID.randomUUID();
        userCommentLikeService.likeComment(first.getId(), deletable);
        userCommentLikeService.likeComment(first.getId(), id);
        userCommentLikeService.likeComment(second.getId(), UUID.randomUUID());
        userCommentLikeService.unlikeComment(first.getId(), deletable);
        userCommentLikeService.unlikeComment(5, UUID.randomUUID());
        likeAggregationService.flush();
        userCommentLikeService.unlikeComment(first.getId(), id);
        likeAggregationService.flush();
        assertEquals(1, likeRepository.count());
    }

    @AfterEach
    void cleanUp(){
        commentRepository.deleteAll();
        projectionRepository.deleteAll();
    }
}
