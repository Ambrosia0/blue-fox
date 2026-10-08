package com.ambrosia.community_service.follow.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.follow.application.CommunityFollowService;
import com.ambrosia.library_policy.policy.Actor;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/community/{id}/follow")
@RequiredArgsConstructor
public class CommunityFollowUserController {
    private final CommunityFollowService communityFollowService;
    
    @ResponseStatus(HttpStatus.CREATED)
    public void followCommunity(
        @PathVariable long id,
        Actor actor
    ) {
        communityFollowService.followCommunity(id, actor);
    }
    
    public void deleteFollow(
        @PathVariable long id,
        Actor actor
    ){
        communityFollowService.removeFollow(id, actor);
    }
}
