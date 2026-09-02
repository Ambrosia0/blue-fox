package com.ambrosia.community_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.community_service.community.model.dto.response.CommunityResponse;
import com.ambrosia.community_service.community.model.dto.response.CommunityUserData;

public interface CommunityQueryRepository {
    Optional<CommunityResponse> findBySlug(String slug);
    CommunityUserData findCommunityUserData(long communityId, UUID userId);
}
