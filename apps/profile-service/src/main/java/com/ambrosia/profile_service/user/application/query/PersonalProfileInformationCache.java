package com.ambrosia.profile_service.user.application.query;

import java.util.UUID;

import com.ambrosia.profile_service.user.api.dto.response.ProfileUserData;

public interface PersonalProfileInformationCache {
    ProfileUserData getById(UUID userId, UUID profileId);
    void evictById(UUID userId, UUID profileId);
}
