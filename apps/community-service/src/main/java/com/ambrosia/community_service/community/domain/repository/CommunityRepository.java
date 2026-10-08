package com.ambrosia.community_service.community.domain.repository;

import java.util.Optional;

import com.ambrosia.community_service.community.domain.entity.Community;

public interface CommunityRepository {
    Optional<Community> findById(Long id);
    Community save(Community community);
    void deleteById(Long id);
    boolean existsBySlug(String slug);
}
