package com.ambrosia.community_service.follow.application;


import com.ambrosia.community_service.follow.api.dto.response.CommunityFollowCreateResponse;
import com.ambrosia.library_policy.policy.Actor;

public interface CommunityFollowService {
    CommunityFollowCreateResponse followCommunity(long communityId, Actor actor);
    void removeFollow(long communityId, Actor actor);
}
