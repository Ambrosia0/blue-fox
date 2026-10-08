package com.ambrosia.profile_service.user.application.query;

import java.util.UUID;

import com.ambrosia.profile_service.user.api.dto.response.CurrentUserProfileResponse;

public interface CurrentUserProfileCache {
    CurrentUserProfileResponse getById(UUID userId);
    void evictById(UUID userId);
}
