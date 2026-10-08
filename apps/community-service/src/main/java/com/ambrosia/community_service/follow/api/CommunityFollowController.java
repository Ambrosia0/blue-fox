package com.ambrosia.community_service.follow.api;

import java.util.UUID;

import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowResponse;
import com.ambrosia.community_service.follow.application.CommunityModeratorFollowService;
import com.ambrosia.community_service.follow.application.query.CommunityFollowQueryService;
import com.ambrosia.library_policy.policy.Actor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/v1/community/{communityId}/follows")
@RestController
public class CommunityFollowController {
    private final CommunityModeratorFollowService communityModeratorFollowService;

    private final CommunityFollowQueryService communityFollowQueryService;

    @GetMapping
    public Slice<CommunityFollowResponse> getFollowRequests(
            @PathVariable long communityId,
            @ModelAttribute FollowFilter filter,
            Actor actor
    ) {
        return communityFollowQueryService.getCommunityFollows(
                actor, 
                communityId, 
                filter, 
                20
            );
    }
    
    @PostMapping("/{userId}")
    public void approveRequest(
            @PathVariable long communityId,
            @PathVariable UUID userId,
            Actor actor
    ) {
        communityModeratorFollowService.approveRequest(
            communityId,
            userId,
            actor
        );
    }
    
    @DeleteMapping("/{userId}")
    public void declineRequest(
            @PathVariable long communityId,
            @PathVariable UUID userId,
            Actor actor
    ){
        communityModeratorFollowService.declineRequest(
            communityId, 
            userId,
            actor
        );
    }
}

