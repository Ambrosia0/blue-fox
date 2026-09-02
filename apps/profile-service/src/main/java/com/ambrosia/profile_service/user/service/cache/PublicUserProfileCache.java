package com.ambrosia.profile_service.user.service.cache;

import com.ambrosia.profile_service.user.model.dto.response.PublicUserProfileResponse;

public interface PublicUserProfileCache {
    PublicUserProfileResponse getByUsername(String username);
    void evictByUsername(String username);
}
