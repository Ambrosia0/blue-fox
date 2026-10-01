package com.ambrosia.community_service.follow.repository.custom;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.ambrosia.community_service.follow.model.entity.CommunityFollow;
import com.ambrosia.community_service.follow.model.entity.key.CommunityFollowKey;


public interface CustomCommunityFollowRepository {
    Optional<CommunityFollow> optionalSave(CommunityFollow communityFollow);
    List<UUID> findByCommunityId(Long communityId, Pageable pageable);
    int returningDelete(CommunityFollowKey communityFollowKey);
}
