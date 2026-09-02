package com.ambrosia.community_service.community.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.model.entity.ScopeLink;
import com.ambrosia.community_service.community.model.entity.keys.ScopeLinkKey;
import com.ambrosia.community_service.community.repository.ScopeLinkRepository;
import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.service.ScopeValidator;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScopeLinkServiceImpl implements ScopeLinkService, ScopeValidator{
    private final ScopeLinkRepository scopeLinkRepository;

    @Override
    public void cleanScopes(long communityId, @Nullable Collection<UUID> exclude) {
        if(exclude != null)
            scopeLinkRepository.cleanScopes(communityId, exclude);
        else
            scopeLinkRepository.cleanScopes(communityId);
    }

    @Override
    public void cleanScopesForUser(long communityId, UUID userId) {
        scopeLinkRepository.cleanScopesForUser(communityId, userId);
    }

    @Override
    public void save(Collection<ScopeLink> links) {
        scopeLinkRepository.saveAll(links);
    }

    @Cacheable(cacheNames = "scopes")
    @Override
    public CommunityScopeResponse getUserScopes(long communityId, UUID requestingUser) {
        return scopeLinkRepository.findByUserIdAndCommunityId(requestingUser, communityId)
            .orElseThrow(() -> new NotEnoughPermissionsException());
    }

    @Override
    public List<ScopeEnum> getScopes() {
        return Arrays.asList(ScopeEnum.values());
    }

    @Override
    public List<CommunityScopeResponse> getCommunityScopes(long communityId) {
        return scopeLinkRepository.getCommunityUsersScopes(communityId);
    }

    @Override
    public boolean hasAnyScope(long communityId, UUID userId) {
        return scopeLinkRepository.isModerator(communityId, userId);
    }

    @Override
    public boolean hasScope(long communityId, ScopeEnum scope, UUID userId) {
        return scopeLinkRepository.existsById(ScopeLinkKey.create(userId, scope.getId(), communityId));
    }
}
