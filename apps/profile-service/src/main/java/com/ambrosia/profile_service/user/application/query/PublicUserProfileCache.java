package com.ambrosia.profile_service.user.application.query;

import com.ambrosia.profile_service.user.api.dto.response.PublicUserProfileResponse;

public interface PublicUserProfileCache {
    PublicUserProfileResponse getByUsername(String username);
    void evictByUsername(String username);
}
