package com.ambrosia.community_service.community.domain.repository;

import java.util.Optional;

import com.ambrosia.community_service.community.domain.entity.Community;
import com.ambrosia.community_service.community.domain.entity.CommunityDelete;

public interface CommunityRepository {
    Optional<Community> findById(Long id);
    Community save(Community community);
    Optional<CommunityDelete> delete(Long id);
    boolean existsBySlug(String slug);
}
