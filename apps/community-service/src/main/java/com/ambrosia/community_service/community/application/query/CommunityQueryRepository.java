package com.ambrosia.community_service.community.application.query;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.community_service.community.application.query.model.CommunityResponse;
import com.ambrosia.community_service.community.application.query.model.CommunityUserDataResponse;

public interface CommunityQueryRepository {
    Optional<CommunityResponse> findBySlug(String slug);
    CommunityUserDataResponse findCommunityUserData(long communityId, UUID userId);
}
