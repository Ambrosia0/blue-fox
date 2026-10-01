package com.ambrosia.community_service.community.service.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.model.dto.response.CommunityScopeResponse;
import com.ambrosia.community_service.community.repository.CommunityRepository;
import com.ambrosia.community_service.community.service.ScopeLinkService;
import com.ambrosia.community_service.community.service.ScopeValidator;
import com.ambrosia.community_service.community.utils.ScopeEnum;
import com.ambrosia.community_service.exception.community.NotEnoughPermissionsException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScopeLinkServiceImpl implements ScopeLinkService, ScopeValidator{
    private final CommunityRepository communityRepository;

    @Override
    public CommunityScopeResponse getUserScopes(long communityId, UUID requestingUser) {
        return communityRepository.findUserScopes(communityId, requestingUser)
            .orElseThrow(() -> new NotEnoughPermissionsException());
    }

    @Override
    public List<ScopeEnum> getScopes() {
        return Arrays.asList(ScopeEnum.values());
    }

    @Override
    public boolean hasAnyScope(long communityId, UUID userId) {
        return communityRepository.isModerator(communityId, userId);
    }

    @Override
    public boolean hasScope(long communityId, ScopeEnum scope, UUID userId) {
        return communityRepository.scopeExists(communityId, scope.getId(), userId);
    }
}
