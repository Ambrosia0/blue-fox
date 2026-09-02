package com.ambrosia.content_service.post.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.post.model.dto.response.PostContentResponse;
import com.ambrosia.content_service.post.service.admin.PostAdminService;
import com.ambrosia.content_service.post.service.user.PostEditorService;
import com.ambrosia.content_service.post.utils.policy.AdminActor;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/post")
public class AdminPostController {
    private final PostAdminService postAdminService;

    private final PostEditorService postEditorService;

    @DeleteMapping("/{id}")
    public void deletePost(
        @PathVariable Long postId){
        postEditorService.deletePost(
            postId,
            new AdminActor()
        );
    }
    
    @GetMapping("/{id}")
    public PostContentResponse getPost(
        @PathVariable Long postId) {
        return postAdminService.getPost(postId);
    }
    
}
