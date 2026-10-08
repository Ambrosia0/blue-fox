package com.ambrosia.content_service.post.api;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
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

import com.ambrosia.content_service.post.api.dto.request.PostCreateRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditRequest;
import com.ambrosia.content_service.post.api.dto.request.PostEditorFilter;
import com.ambrosia.content_service.post.api.dto.response.PostEditorContentResponse;
import com.ambrosia.content_service.post.api.dto.response.PostEditorViewResponse;
import com.ambrosia.content_service.post.application.PostEditorService;
import com.ambrosia.library_policy.policy.Actor;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/me/post")
public class PostEditorController {
    
    private final PostEditorService postEditorService;

    @GetMapping("/{postId}")
    public PostEditorContentResponse getContent(
            @PathVariable long postId,
            Actor actor){
        return postEditorService.getContent(postId, actor);
    }

    @PatchMapping("/{postId}")
    public void editPost(
            @PathVariable long postId,
            @RequestBody @Valid PostEditRequest postRequest,
            Actor actor){
        postEditorService.editPost(actor, postId, postRequest);
    }

    @DeleteMapping("/{postId}") 
    public void deletePost(
            @PathVariable long postId,
            Actor actor){
        postEditorService.deleteDraftPost(postId, actor);
    }

    @PostMapping
    public PostEditorViewResponse createPost(
            @RequestBody @Valid PostCreateRequest postCreateRequest,
            Actor actor){
        return postEditorService.createPost(actor, postCreateRequest);
    }
    

    @PostMapping("/{postId}/publish")
    public void publishPost(
            @PathVariable long postId,
            Actor actor) {
        postEditorService.publishPost(actor, postId);
    }

    @PostMapping("/{postId}/unpublish")
    public void unpublishPost(
            @PathVariable long postId,
            Actor actor) {
        postEditorService.unpublishPost(actor, postId);
    } 
    
    
    @GetMapping
    public Slice<PostEditorViewResponse> getUnpublishedPosts(
        @ModelAttribute PostEditorFilter postEditorFilter,
        @PageableDefault(page = 0, size = 20) Pageable pageable,
        Actor actor) {
        return postEditorService.getUnpublishedPosts(actor, postEditorFilter, pageable);
    }
}