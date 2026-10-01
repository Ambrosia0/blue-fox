package com.ambrosia.content_service.post.controller;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.post.model.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.model.dto.request.PostEditorFilter;
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
        var userId = UUID.fromString(jwt.getSubject());
        postEditorService.deleteDraftPost(
            postId,
            new UserActor(userId)
        );
    }

    @PostMapping
    public PostEditorViewResponse createPost(
            @RequestBody @Valid PostCreateRequest postCreateRequest,
            @AuthenticationPrincipal Jwt jwt){
        var userId = UUID.fromString(jwt.getSubject());
        return postEditorService.createPost(
            userId, 
            new UserActor(userId),
            postCreateRequest
        );
    }
    

    @PostMapping("/{postId}/publish")
    public void publishPost(
            @PathVariable long postId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        postEditorService.publishPost(
            new UserActor(userId), 
            postId
        );
    }

    @PostMapping("/{postId}/unpublish")
    public void unpublishPost(
            @PathVariable long postId,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = UUID.fromString(jwt.getSubject());
        postEditorService.unpublishPost(
            userId, 
            postId
        );
    } 
    
    
    @GetMapping
    public Slice<PostEditorViewResponse> getUnpublishedPosts(
        @ModelAttribute PostEditorFilter postEditorFilter,
        @PageableDefault(page = 0, size = 20) Pageable pageable,
        @AuthenticationPrincipal Jwt jwt) {
        return postEditorService.getUnpublishedPosts(
            UUID.fromString(jwt.getSubject()),
            postEditorFilter,
            pageable
        );
    }
}