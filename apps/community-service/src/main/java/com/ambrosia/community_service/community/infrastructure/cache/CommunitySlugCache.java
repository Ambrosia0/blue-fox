package com.ambrosia.community_service.community.infrastructure.cache;

import com.ambrosia.community_service.community.application.query.model.CommunityResponse;

public interface CommunitySlugCache {
    CommunityResponse findCommunity(String slug);
    void evictCommunity(String slug);
}
