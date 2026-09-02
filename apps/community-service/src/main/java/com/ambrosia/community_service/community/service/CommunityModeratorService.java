package com.ambrosia.community_service.community.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;

public interface CommunityModeratorService {
    void banUser(long communityId, UUID requestingUser, UUID userToBan, Instant beforeDate);
    void unbanUser(long communityId, UUID requestingUser, UUID userToUnban);
    List<CommunityScopeResponse> getUsersScopesForCommunity(long communityId, UUID requestingUser);
}
