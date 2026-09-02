package com.ambrosia.content_service.follow.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.follow.model.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.service.UserFollowService;

import lombok.RequiredArgsConstructor;

import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@RequestMapping("/api/me/follow")
@RequiredArgsConstructor
public class UserFollowController {
    private final UserFollowService userFollowService;

    @GetMapping("/user")
    public Slice<UserFollowResponse> getUserFollows(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(required = false, defaultValue = "0") int page) {
        return userFollowService.getFollows(UUID.fromString(jwt.getSubject()), page);
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/user/{userId}")
    public void followUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal Jwt jwt) {
        userFollowService.followUser(UUID.fromString(jwt.getSubject()), userId);
    }
    
    @DeleteMapping("/user/{userId}")
    public void removeFollow(
        @PathVariable UUID userId,
        @AuthenticationPrincipal Jwt jwt){
        userFollowService.removeFollow(UUID.fromString(jwt.getSubject()), userId);
    }
}
