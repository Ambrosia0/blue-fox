package com.ambrosia.community_service.follow.domain.repository;

import java.util.UUID;

public interface CommunityFollowRequestRepository {
    boolean approve(UUID userId, Long communityId);
    boolean decline(UUID userId, Long communityId);
}
