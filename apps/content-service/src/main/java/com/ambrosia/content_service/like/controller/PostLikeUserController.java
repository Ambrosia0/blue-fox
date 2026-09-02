package com.ambrosia.content_service.like.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.like.service.LikeUserService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/post/{id}/like")
public class PostLikeUserController {
    private final LikeUserService likeUserService;
    
    @PostMapping
    public void likePost(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt) {
        likeUserService.likePost(id, UUID.fromString(jwt.getSubject()));
    }

    @DeleteMapping
    public void unlikePost(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt) {
        likeUserService.unlikePost(id, UUID.fromString(jwt.getSubject()));
    }

    @GetMapping
    public boolean isLiked(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt) {
        return likeUserService.isLiked(id, UUID.fromString(jwt.getSubject()));
    }
    
}
