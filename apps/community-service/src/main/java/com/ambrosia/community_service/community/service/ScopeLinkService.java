package com.ambrosia.community_service.community.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.utils.ScopeEnum;

import jakarta.annotation.Nullable;

public interface ScopeLinkService {
    void cleanScopes(long communityId, @Nullable Collection<UUID> exclude);
    void cleanScopesForUser(long communityId, UUID userId);
    void save(Collection<ScopeLink> links);
    boolean hasScope(long communityId, ScopeEnum scope, UUID userId);
    boolean hasAnyScope(long communityId, UUID userId);
    CommunityScopeResponse getUserScopes(long communityId, UUID requestingUser);
    List<CommunityScopeResponse> getCommunityScopes(long communityId);
    List<ScopeEnum> getScopes();
}
