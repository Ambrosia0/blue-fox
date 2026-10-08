package com.ambrosia.profile_service.user.application.query.impl;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.ambrosia.profile_service.exception.api.user.UserDoesntExistException;
import com.ambrosia.profile_service.user.api.dto.response.PublicUserProfileResponse;
import com.ambrosia.profile_service.user.application.query.PublicUserProfileCache;
import com.ambrosia.profile_service.user.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PublicUserProfileCacheImpl implements PublicUserProfileCache{
    private final UserRepository userRepository;
    
    @Cacheable(cacheNames = "users")
    @Override
    public PublicUserProfileResponse getByUsername(String username) {
        return userRepository.findPublicProfileByUsername(username)
            .orElseThrow(() -> new UserDoesntExistException());
    }

    @CacheEvict(cacheNames = "users")
    @Override
    public void evictByUsername(String username) {}
}
