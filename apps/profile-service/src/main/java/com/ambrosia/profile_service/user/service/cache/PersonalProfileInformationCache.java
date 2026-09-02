package com.ambrosia.profile_service.user.service.cache;

import java.util.UUID;

import com.ambrosia.profile_service.user.model.dto.response.ProfileUserData;

public interface PersonalProfileInformationCache {
    ProfileUserData getById(UUID userId, UUID profileId);
    void evictById(UUID userId, UUID profileId);
}
