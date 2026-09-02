package com.ambrosia.content_service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.ambrosia.content_service.attachment.service.PostAttachmentUserService;
import com.ambrosia.content_service.config.SecurityConfig;
import com.ambrosia.content_service.follow.service.UserFollowService;
import com.ambrosia.content_service.grpc.CommunityService;
import com.ambrosia.content_service.grpc.ProfileService;
import com.ambrosia.content_service.like.service.LikeUserService;
import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.service.admin.PostAdminService;
import com.ambrosia.content_service.post.service.user.PostCommunityModeratorService;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.search.model.dto.EventFilter;

@Import({SecurityConfig.class})
@WebMvcTest
public class ApplicationControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean PostUserService postUserService;
    @MockitoBean PostEditorService postEditorService;
    @MockitoBean PostAttachmentUserService attachmentService;
    @MockitoBean LikeUserService likeUserService;
    @MockitoBean PostCommunityModeratorService communityModeratorService;
    @MockitoBean ProfileService profileService;
    @MockitoBean CommunityService communityService;
    @MockitoBean UserFollowService userFollowService;
    @MockitoBean PostAdminService postAdminService;
    
    public static final Jwt jwt = Jwt.withTokenValue("token")
        .subject(UUID.randomUUID().toString())
        .claim("realm_access", Map.of("roles", List.of("user")))
        .issuedAt(Instant.now())
        .expiresAt(Instant.now().plusSeconds(2048))
        .header("test", "testHEader")
        .build();
        
    @BeforeEach
    void init(WebApplicationContext webApplicationContext){
        when(postUserService.search(any(EventFilter.class), any(UUID.class), anyInt())).thenReturn(List.of());
        when(likeUserService.isLiked(anyLong(), any())).thenReturn(true);
        when(communityService.isUserAllowed(any(UUID.class), anyString(), anyLong())).thenReturn(true);
        when(profileService.isUserExist(any(UUID.class))).thenReturn(true);
        when(userFollowService.getFollows(any(UUID.class), anyInt())).thenReturn(Page.empty());
        when(postAdminService.getPost(anyLong())).thenReturn(new PostContentResponse());
    }

    @Test
    void postLikeControllerTest() throws Exception{
        mockMvc.perform(
            post("/api/user/post/2/like")
            .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @Test
    void postUnlikeControllerTest() throws Exception{
        mockMvc.perform(
            delete("/api/user/post/2/like")
                .with(jwt().jwt(jwt))
        ).andExpect(status().isOk());
    }

    @Test
    void postLikeGetControllerTest() throws Exception{
        mockMvc.perform(
            get("/api/user/post/2/like")
                .with(jwt().jwt(jwt))   
        ).andExpect(status().isOk());
    }

    @Test
    void postGetControllerTest() throws Exception{
        when(postUserService.getPost(anyLong(), any(UUID.class))).thenReturn(PostContentResponse.builder().build());
        mockMvc.perform(
            get("/api/public/post/2")
        ).andExpect(status().isOk());
    }

    @Test
    void postGetMultipleControllerTest(){
    }

}
