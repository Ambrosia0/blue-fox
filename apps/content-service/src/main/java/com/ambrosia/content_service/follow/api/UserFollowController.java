package com.ambrosia.content_service.follow.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.content_service.follow.api.dto.UserFollowResponse;
import com.ambrosia.content_service.follow.application.UserFollowService;

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
@RequestMapping("/api/v1/me/follow/user")
@RequiredArgsConstructor
public class UserFollowController {
    private final UserFollowService userFollowService;

    @GetMapping
    public Slice<UserFollowResponse> getUserFollows(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(required = false, defaultValue = "0") int page) {
        return userFollowService.getFollows(UUID.fromString(jwt.getSubject()), page);
    }
    
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{userId}")
    public void followUser(
        @PathVariable UUID userId,
        @AuthenticationPrincipal Jwt jwt) {
        userFollowService.followUser(UUID.fromString(jwt.getSubject()), userId);
    }
    
    @DeleteMapping("/{userId}")
    public void removeFollow(
        @PathVariable UUID userId,
        @AuthenticationPrincipal Jwt jwt){
        userFollowService.removeFollow(UUID.fromString(jwt.getSubject()), userId);
    }
}
