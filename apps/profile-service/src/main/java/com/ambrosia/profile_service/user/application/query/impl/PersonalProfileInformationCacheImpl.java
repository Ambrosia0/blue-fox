package com.ambrosia.profile_service.user.application.query.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.user.api.dto.response.ProfileUserData;
import com.ambrosia.profile_service.user.application.query.PersonalProfileInformationCache;
import com.ambrosia.profile_service.user.application.query.UserQueryRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PersonalProfileInformationCacheImpl implements PersonalProfileInformationCache{
    private final UserQueryRepository userQueryRepository;

    @Cacheable(cacheNames = "profile-userdata")
    @Override
    public ProfileUserData getById(UUID userId, UUID profileId) {
        return userQueryRepository.findUserData(userId, profileId);
    }

    @CacheEvict(cacheNames = "profile-userdata")
    @Override
    public void evictById(UUID userId, UUID profileId) {}
}
