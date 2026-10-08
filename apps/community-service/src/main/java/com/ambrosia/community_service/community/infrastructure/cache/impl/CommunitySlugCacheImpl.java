package com.ambrosia.community_service.community.infrastructure.cache.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.community.application.query.CommunityQueryRepository;
import com.ambrosia.community_service.community.application.query.model.CommunityResponse;
import com.ambrosia.community_service.community.infrastructure.cache.CommunitySlugCache;
import com.ambrosia.community_service.exception.community.CommunityDoesntExistException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunitySlugCacheImpl implements CommunitySlugCache{
    private final CommunityQueryRepository communityQueryRepository;

    @Cacheable(cacheNames = "community", key = "#slug")
    @Override
    public CommunityResponse findCommunity(String slug) {
        return communityQueryRepository.findBySlug(slug)
            .orElseThrow(() -> new CommunityDoesntExistException());
    }

    @CacheEvict(cacheNames = "community", key="#slug")
    @Override
    public void evictCommunity(String slug) {}
}
