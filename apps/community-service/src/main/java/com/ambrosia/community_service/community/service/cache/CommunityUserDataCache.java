package com.ambrosia.community_service.community.service.cache;

import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityUserData;

public interface CommunityUserDataCache {
    CommunityUserData findUserData(UUID userId, Long communityId);
    void evictUserData(UUID userId, Long communityId);
}
