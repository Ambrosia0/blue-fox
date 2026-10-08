package com.ambrosia.profile_service.user.application.query.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.user.api.dto.response.CurrentUserProfileResponse;
import com.ambrosia.profile_service.user.application.query.CurrentUserProfileCache;
import com.ambrosia.profile_service.user.application.query.UserQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CurrentUserProfileCacheImpl implements CurrentUserProfileCache{
    private final UserQueryRepository userQueryRepository;

    @Cacheable(cacheNames = "user")
    @Override
    public CurrentUserProfileResponse getById(UUID userId) {
        return userQueryRepository.findProfileById(userId);
    }

    @CacheEvict(cacheNames = "user")
    @Override
    public void evictById(UUID userId) {}
}
