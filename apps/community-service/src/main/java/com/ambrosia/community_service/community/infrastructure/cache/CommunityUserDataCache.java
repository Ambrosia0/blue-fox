package com.ambrosia.community_service.community.infrastructure.cache;

import java.util.UUID;

import com.ambrosia.community_service.community.application.query.model.CommunityUserDataResponse;

public interface CommunityUserDataCache {
    CommunityUserDataResponse findUserData(UUID userId, Long communityId);
    void evictUserData(UUID userId, Long communityId);
}
