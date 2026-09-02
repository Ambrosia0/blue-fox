package com.ambrosia.community_service.follow.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.service.CommunityFollowService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user/follow/community")
@RequiredArgsConstructor
public class CommunityFollowController {
    private final CommunityFollowService communityFollowService;

    @GetMapping
    public Slice<CommunityFollow> getCommunityFollows(
        @AuthenticationPrincipal Jwt jwt,
        @RequestParam(required = false, defaultValue = "0") int page)  {
        return communityFollowService.getFollows(UUID.fromString(jwt.getSubject()), page);
    }
    
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/community/{id}")
    public void followCommunity(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt) {
        communityFollowService.followCommunity(id, UUID.fromString(jwt.getSubject()));
    }
    
    @DeleteMapping("/community/{id}")
    public void deleteFollow(
        @PathVariable long id,
        @AuthenticationPrincipal Jwt jwt){
        communityFollowService.removeFollow(id, UUID.fromString(jwt.getSubject()));
    }

    @GetMapping("/community/{id}")
    public List<UUID> getFollowedUsers(
        @PathVariable long id,
        @RequestParam(required = false, defaultValue = "0") int page) {
        return communityFollowService.getFollowedUsers(id, page);
    }
    
}
