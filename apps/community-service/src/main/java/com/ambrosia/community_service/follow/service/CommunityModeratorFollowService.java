package com.ambrosia.community_service.follow.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;

public interface CommunityModeratorFollowService {
    List<CommunityFollowRequest> getRequests(Long communityId, UUID requestingUser, int page);
    void approveRequest(Long communityId, UUID approvedUser, UUID requestingUser);
    void declineRequest(Long communityId, UUID declinedUser, UUID requestingUser);
}
