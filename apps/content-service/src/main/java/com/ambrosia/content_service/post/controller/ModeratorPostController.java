package com.ambrosia.content_service.post.controller;

import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.post.service.user.PostCommunityModeratorService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user/post")
public class ModeratorPostController {
    private final PostCommunityModeratorService communityModeratorService;
    
    @DeleteMapping("/{id}")
    public void deletePost(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt) {
        communityModeratorService.deletePost(UUID.fromString(jwt.getSubject()), id);
    }
}
