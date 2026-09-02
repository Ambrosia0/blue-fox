package com.ambrosia.community_service.community.repository.custom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;

public interface CustomScopeLinkRepository {
    List<CommunityScopeResponse> getCommunityUsersScopes(long communityId);
    Optional<CommunityScopeResponse> findByUserIdAndCommunityId(UUID userId, long communityId);
}
