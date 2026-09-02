package com.ambrosia.community_service.community.service.cache;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;

public interface CommunitySlugCache {
    CommunityResponse findCommunity(String slug);
    void evictCommunity(String slug);
}
