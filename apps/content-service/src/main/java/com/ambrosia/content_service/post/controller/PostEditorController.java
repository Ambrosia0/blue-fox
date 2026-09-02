package com.ambrosia.content_service.post.controller;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.model.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.UserActor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/me/post")
public class PostEditorController {
    
    private final PostEditorService postEditorService;

    @GetMapping("/{postId}")
    public PostEditorContentResponse getContent(
        @PathVariable long postId,
        @AuthenticationPrincipal Jwt jwt){
        return postEditorService.getContent(
                postId,
                UUID.fromString(jwt.getSubject()));
    }

    @PatchMapping("/{postId}")
    public void editPost(
        @PathVariable long postId,
        @RequestBody @Valid PostEditRequest postRequest,
        @AuthenticationPrincipal Jwt jwt){
        postEditorService.editPost(UUID.fromString(jwt.getSubject()), postId, postRequest);
    }

    @DeleteMapping("/{postId}") 
    public void deletePost(
        @PathVariable long postId,
        @AuthenticationPrincipal Jwt jwt){
        postEditorService.deletePost(
            postId,
            new UserActor(UUID.fromString(jwt.getSubject()))
        );
    }


    @PostMapping
    public PostEditorViewResponse createPost(
        @RequestBody @Valid PostCreateRequest postCreateRequest,
        @AuthenticationPrincipal Jwt jwt){
        return postEditorService.createPost(UUID.fromString(jwt.getSubject()), postCreateRequest);
    }
    

    @PostMapping("/{postId}/publish")
    public void publishPost(
        @PathVariable long postId,
        @AuthenticationPrincipal Jwt jwt) {
        postEditorService.publishPost(UUID.fromString(jwt.getSubject()), postId);
    }
    
    
    @GetMapping
    public Page<PostEditorViewResponse> getUnpublishedPosts(
        @PageableDefault(sort = "createdAt", size = 20, page = 0, direction = Direction.DESC) Pageable pageable,
        @AuthenticationPrincipal Jwt jwt) {
        return postEditorService.getUnpublishedPosts(UUID.fromString(jwt.getSubject()), pageable);
    }
}