package com.ambrosia.community_service.community.service;

import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.utils.ScopeEnum;

public interface ScopeLinkService {
    boolean hasScope(long communityId, ScopeEnum scope, UUID userId);
    boolean hasAnyScope(long communityId, UUID userId);
    CommunityScopeResponse getUserScopes(long communityId, UUID requestingUser);
    List<CommunityScopeResponse> getCommunityScopes(long communityId);
    List<ScopeEnum> getScopes();
}
