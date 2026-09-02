package com.ambrosia.community_service.follow.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;
import com.ambrosia.community_service.follow.service.CommunityModeratorFollowService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/user/follow/community/{communityId}/requests")
@RestController
public class CommunityFollowModeratorController {
    private final CommunityModeratorFollowService communityModeratorFollowService;

    @GetMapping
    public List<CommunityFollowRequest> getFollowRequests(
        @PathVariable long communityId,
        @RequestParam(required = false, defaultValue = "0") int page,
        @AuthenticationPrincipal Jwt jwt) {
        return communityModeratorFollowService.getRequests(
            communityId,
            UUID.fromString(jwt.getSubject()),
            page
        );
    }
    
    @PostMapping("/{userId}")
    public void approveRequest(
        @PathVariable long communityId,
        @PathVariable UUID userId,
        @AuthenticationPrincipal Jwt jwt) {
        communityModeratorFollowService.approveRequest(
            communityId,
            userId,
            UUID.fromString(jwt.getSubject())
        );
    }
    
    @DeleteMapping("/{userId}")
    public void declineRequest(
        @PathVariable long communityId,
        @PathVariable UUID userId,
        @AuthenticationPrincipal Jwt jwt){
        communityModeratorFollowService.declineRequest(
            communityId, 
            userId,
            UUID.fromString(jwt.getSubject())
        );
    }
}

