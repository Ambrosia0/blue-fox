package com.ambrosia.community_service.follow.api;

import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ambrosia.community_service.follow.api.dto.request.FollowFilter;
import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowUserResponse;
import com.ambrosia.community_service.follow.application.query.CommunityFollowQueryService;
import com.ambrosia.library_policy.policy.Actor;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@RestController 
@RequestMapping("/api/v1/community/follows")
public class CommunityFollowViewController {
    private final CommunityFollowQueryService communityFollowQueryService;

    public Slice<CommunityFollowUserResponse> getFollows(
        @PathVariable long id,
        @ModelAttribute FollowFilter filter,
        Actor actor
    ) {
        return communityFollowQueryService.getUserFollows(actor, filter, 20);
    }
}
