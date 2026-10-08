package com.ambrosia.community_service.follow.domain.repository;

import java.util.Optional;
import java.util.UUID;

import com.ambrosia.community_service.follow.domain.entity.CommunityFollow;

public interface CommunityFollowRepository {
    void persist(CommunityFollow communityFollow);
    boolean remove(UUID userId, Long communityId);
    Optional<CommunityFollow> findById(UUID userId, Long communityId);
}
