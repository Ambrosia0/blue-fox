package com.ambrosia.profile_service.user.service.cache;

import java.util.UUID;

import com.ambrosia.profile_service.user.model.dto.response.CurrentUserProfileResponse;

public interface CurrentUserProfileCache {
    CurrentUserProfileResponse getById(UUID userId);
    void evictById(UUID userId);
}
