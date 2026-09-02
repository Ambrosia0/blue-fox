package com.ambrosia.profile_service.user.service.cache.impl;

import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.user.model.dto.response.ProfileUserData;
import com.ambrosia.profile_service.user.repository.UserQueryRepository;
import com.ambrosia.profile_service.user.service.cache.PersonalProfileInformationCache;

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
