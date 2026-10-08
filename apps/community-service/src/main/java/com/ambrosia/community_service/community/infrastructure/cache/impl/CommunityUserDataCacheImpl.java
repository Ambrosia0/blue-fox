package com.ambrosia.community_service.community.infrastructure.cache.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.application.query.CommunityQueryRepository;
import com.ambrosia.community_service.community.application.query.model.CommunityUserDataResponse;
import com.ambrosia.community_service.community.infrastructure.cache.CommunityUserDataCache;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityUserDataCacheImpl implements CommunityUserDataCache{
    private final CommunityQueryRepository communityQueryRepository;

    @Cacheable(cacheNames = "community-userdata", key = "#community + '_' + #userId")
    @Override
    public CommunityUserDataResponse findUserData(UUID userId, Long communityId) {
        return communityQueryRepository.findCommunityUserData(communityId, userId);
    }

    @CacheEvict(cacheNames = "community-userdata", key = "#community + '_' + #userId")
    @Override
    public void evictUserData(UUID userId, Long communityId) {}
}
