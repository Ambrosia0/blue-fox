package com.ambrosia.content_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.WebApplicationContext;

import com.ambrosia.community_service.grpc.ScopeCheckResponse;
import com.ambrosia.community_service.grpc.CommunityServiceGrpc.CommunityServiceBlockingStub;
import com.ambrosia.content_service.attachment.model.dto.response.AttachmentUploadResponse;
import com.ambrosia.content_service.attachment.repository.PostAttachmentRepository;
import com.ambrosia.content_service.like.repository.PostLikeRepository;
import com.ambrosia.content_service.like.service.LikeAggregationService;
import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.model.dto.response.PreviewWithScoreResponse;
import com.ambrosia.content_service.post.repository.PostRepository;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.search.model.entity.elastic.PostElastic;
import com.ambrosia.content_service.search.repository.elastic.ElasticPostRepository;
import com.ambrosia.content_service.util.FileMetadataFactory;
import com.ambrosia.content_service.util.PostTemplate;
import com.ambrosia.outbox.elastic.ElasticsearchOutboxRelay;
import com.ambrosia.profile_service.grpc.UserExistenceResponse;

import com.ambrosia.profile_service.grpc.ProfileServiceGrpc.ProfileServiceBlockingStub;

import tools.jackson.databind.ObjectMapper;

public class ContentServiceApplicationTests extends BaseIntegrationTest{
    @Autowired PostEditorService postEditorService;
    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ElasticsearchOperations elasticsearchOperations;
    @Autowired ElasticPostRepository elasticPostRepository;
    @Autowired LikeAggregationService likeAggregationService;

    @Autowired RestClient testRestClient;

    @MockitoBean ProfileServiceBlockingStub profileService;
    @MockitoBean CommunityServiceBlockingStub communityService;

    @Autowired ElasticsearchOutboxRelay relay;
    @Autowired PostRepository postRepository;
    @Autowired PostLikeRepository postLikeRepository;
    @Autowired PostAttachmentRepository postAttachmentRepository;

    public static final Jwt jwt = Jwt.withTokenValue("token")
        .subject(UUID.randomUUID().toString())
        .claim("realm_access", Map.of("roles", List.of("user")))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(2048))
        .header("test", "testHEader")
        .build();

    private final static PostCreateRequest post = new PostCreateRequest("test title", null);
    
    @BeforeAll
    void init(WebApplicationContext context){
        when(communityService.isUserAllowed(any())).thenReturn(ScopeCheckResponse.newBuilder().setIsAllowed(true).build());
        when(profileService.isUserExist(any())).thenReturn(UserExistenceResponse.newBuilder().setIsExist(true).build());
        var res = postEditorService.createPost(UUID.fromString(jwt.getSubject()), new PostCreateRequest("Teeeest title", null));
        postEditorService.editPost(res.authorId(), res.id(), new PostEditRequest("Test title", PostTemplate.template, List.of("#test"), 0L));
        postEditorService.publishPost(UUID.fromString(jwt.getSubject()), res.id());
        relay.flush();

    }

	@Test
    @WithMockUser(roles = "USER")
	void userPostGetTest() throws Exception {
        var resp = mockMvc.perform(
            get("/api/public/post")
            .param("type", "LATEST")
            .accept(MediaType.APPLICATION_JSON)
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        objectMapper.readValue(resp, PreviewWithScoreResponse[].class);
    }


    @Test
    @WithMockUser(roles = "USER")
	void userPostSearchTest() throws Exception{
        mockMvc.perform(
            get("/api/public/post?searchString={searchStr}", "test")
        ).andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void userSpecificPostGetTest() throws Exception{
        mockMvc.perform(
            get("/api/public/post?authorId={user}", UUID.fromString(jwt.getSubject()))
            .param("type", "LATEST")
        ).andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void userPostContentGetTest() throws Exception{
        var resp = mockMvc.perform(
            get("/api/public/post")
            .param("type", "LATEST")
            .accept(MediaType.APPLICATION_JSON)
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        var body = objectMapper.readValue(resp, PreviewWithScoreResponse[].class);

        mockMvc.perform(
            get("/api/public/post/{id}", body[0].postViewResponse().id())
        ).andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void userEditorGetContentTest() throws Exception{
        var id = objectMapper.readValue(
            mockMvc.perform(
                post("/api/me/post")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(post))
                )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), 
            PostEditorViewResponse.class)
            .id();
        mockMvc.perform(
            get("/api/me/post/{id}", id)
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void userEditorPostEditTest() throws Exception{
        var resp = objectMapper.readValue(
            mockMvc.perform(
                post("/api/me/post")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(post))
                )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), 
            PostEditorViewResponse.class);
        mockMvc.perform(
            patch("/api/me/post/{id}", resp.id())
            .with(jwt().jwt(jwt))
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(new PostEditRequest("Test title", PostTemplate.template, List.of("#test"), 0L)))
        ).andExpect(status().isOk());
    }

    @Test
    void userEditorDeletePostTest() throws Exception{
        var id = objectMapper.readValue(
            mockMvc.perform(
                post("/api/me/post")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(post))
                )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), 
            PostEditorViewResponse.class)
            .id();
        mockMvc.perform(
            delete("/api/me/post/{id}", id)
                .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @WithMockUser(roles = "USER")
    @Test
    void userEditorMediaAttachTest() throws Exception{
        var id = objectMapper.readValue(
            mockMvc.perform(
                post("/api/me/post")
                    .with(jwt().jwt(jwt))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(post))
                )
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString(), 
            PostEditorViewResponse.class)
            .id();
        var file = FileMetadataFactory.fileMetadata();
        var uploadResp = mockMvc.perform(
            post("/api/me/post/{id}/attachment", id)
            .contentType(MediaType.APPLICATION_JSON)
            .with(jwt().jwt(jwt))
            .content(objectMapper.writeValueAsString(file))
        )
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        var attachResp = objectMapper.readValue(uploadResp, AttachmentUploadResponse.class);
        testRestClient.put()
            .uri(URI.create(attachResp.uploadUrl()))
            .contentType(MediaType.parseMediaType(file.contentType().getMimeType()))
            .contentLength(file.fileSize())
            .header("x-amz-checksum-md5", file.md5())
            .body(Files.readAllBytes(FileMetadataFactory.testImagePath))
            .retrieve()
            .toBodilessEntity();
            
        mockMvc.perform(
            post(
                "/api/me/post/{postId}/attachment/{attachmentId}", 
                id, 
                attachResp.attachmentId()
            )
            .with(jwt().jwt(jwt))
        )
        .andExpect(status().isNoContent());
    }

    @Test
    void userPostPlaceLikeTest() throws Exception{
        var req = mockMvc.perform(
            get("/api/public/post")
            .accept(MediaType.APPLICATION_JSON)
            .param("type", "LATEST")
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        var body = objectMapper.readValue(req, PreviewWithScoreResponse[].class);

        mockMvc.perform(
            post("/api/user/post/{id}/like", body[0].postViewResponse().id())
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
        likeAggregationService.flush();
    }

    @WithMockUser(roles = "USER")
    @Test
    void userPostUnplaceLikeTest() throws Exception{
        
        var req = mockMvc.perform(
            get("/api/public/post")
            .accept(MediaType.APPLICATION_JSON)
            .param("type", "LATEST")
        )
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
        var body = objectMapper.readValue(req, PreviewWithScoreResponse[].class);

        mockMvc.perform(
            post("/api/user/post/{id}/like", body[0].postViewResponse().id())
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());

        mockMvc.perform(
            delete("/api/user/post/{id}/like", body[0].postViewResponse().id())
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
        likeAggregationService.flush();
    }
    
    
    @AfterAll
    void cleanUp(){
        postAttachmentRepository.deleteAll();
        postLikeRepository.deleteAll();
        postRepository.deleteAll();
        elasticPostRepository.deleteAll();
        elasticsearchOperations.indexOps(PostElastic.class).refresh();
    }
}
