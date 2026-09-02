package com.ambrosia.content_service.community.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.content_service.community.model.dto.CommunityUserData;

public interface CommunityQueryRepository {
    Optional<CommunityUserData> findCommunityUserDataByCommunityId(long communityId, UUID userId);
}
