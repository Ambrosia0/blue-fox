package com.ambrosia.community_service.follow.repository.custom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;


public interface CustomCommunityFollowRepository {
    Optional<CommunityFollow> optionalSave(CommunityFollow communityFollow);
    Slice<CommunityFollow> findByUserId(UUID userId, Pageable pageable);
    List<UUID> findByCommunityId(Long communityId, Pageable pageable);
    int returningDelete(CommunityFollowKey communityFollowKey);
}
