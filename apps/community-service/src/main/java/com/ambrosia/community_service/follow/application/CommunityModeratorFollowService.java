package com.ambrosia.community_service.follow.application;

import java.util.UUID;

import com.ambrosia.library_policy.policy.Actor;

public interface CommunityModeratorFollowService {
    void approveRequest(Long communityId, UUID approvedUser, Actor actor);
    void declineRequest(Long communityId, UUID declinedUser, Actor actor);
}
