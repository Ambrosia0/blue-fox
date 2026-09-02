package com.ambrosia.comment_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.ambrosia.comment_service.comment.model.dto.EventFilter;
import com.ambrosia.comment_service.comment.service.AdminCommentService;
import com.ambrosia.comment_service.comment.service.CommentQueryService;
import com.ambrosia.comment_service.comment.service.ModeratorCommentService;
import com.ambrosia.comment_service.comment.service.UserCommentLikeService;
import com.ambrosia.comment_service.comment.service.UserCommentService;
import com.ambrosia.comment_service.config.SecurityConfig;
import com.ambrosia.comment_service.post.service.PostProjectionService;
import com.ambrosia.comment_service.utils.factory.CommentRequestFactory;

import tools.jackson.databind.ObjectMapper;

@ActiveProfiles({"test"})
@WebMvcTest
@Import(SecurityConfig.class)
public class CommentServiceControllerTests {
    @MockitoBean UserCommentService userCommentService;
    @MockitoBean CommentQueryService commentQueryService;

    @Autowired ObjectMapper objectMapper;

    @Autowired MockMvc mockMvc;

    @MockitoBean ModeratorCommentService moderatorCommentService;
    @MockitoBean UserCommentLikeService commentLikeService;
    @MockitoBean PostProjectionService postProjectionService;
    @MockitoBean AdminCommentService adminCommentService;

    public static final Jwt jwt = Jwt.withTokenValue("token")
        .subject(UUID.randomUUID().toString())
        .claim("realm_access", Map.of("roles", List.of("user")))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(2048))
        .header("test", "testHEader")
        .build();

    public static final Jwt adminJwt = Jwt.withTokenValue("token")
        .subject(UUID.randomUUID().toString())
        .claim("realm_access", Map.of("roles", List.of("admin")))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(2048))
        .header("test", "testHEader")
        .build();

    // @BeforeEach
    // void init(){
    // }
    
    @Test
    void userGetCommentsTest() throws Exception{
        when(commentQueryService.getCommentsForPost(
            anyLong(),
            any(EventFilter.class), 
            any()))
        .thenReturn(List.of());
        mockMvc.perform(
            get("/api/public/comment/2")
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @Test
    void userGetCommentTreeTest() throws Exception{
        mockMvc.perform(
            get("/api/public/comment/2/tree")
        ).andExpect(status().isOk());
    }

    @Test
    void userCreateComment() throws Exception{
        when(postProjectionService.isExists(anyLong())).thenReturn(true);        
        mockMvc.perform(multipart("/api/user/comment")
            .content(
                objectMapper.writeValueAsString(
                    CommentRequestFactory.createCommentRequest(ThreadLocalRandom.current().nextLong())
                )
            )
            .contentType(MediaType.APPLICATION_JSON)
            .with(jwt().jwt(jwt))
        ).andExpect(status().isCreated());
    }

    @Test
    void userLikeCommentTest() throws Exception{
        mockMvc.perform(
            post("/api/user/comment/2/like")
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @Test
    void userUnlikeCommentTest() throws Exception{
        mockMvc.perform(
            delete("/api/user/comment/2/like")
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @Test
    void adminDeleteCommentTest() throws Exception{
        mockMvc.perform(
            delete("/api/admin/comment/2")
            .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
        ).andExpect(status().isOk());
    }

}
