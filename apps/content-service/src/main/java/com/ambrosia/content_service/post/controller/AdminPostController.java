package com.ambrosia.content_service.post.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.post.service.user.PostUserService;
import com.ambrosia.content_service.post.utils.policy.AdminActor;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/post/{id}")
public class AdminPostController {
    private final PostUserService postUserService;

    @DeleteMapping
    public void deletePost(
        @PathVariable Long postId,
        @AuthenticationPrincipal Jwt jwt
    ){
        postUserService.deletePost(
            postId,
            new AdminActor(UUID.fromString(jwt.getSubject()))
        );
    }
}
