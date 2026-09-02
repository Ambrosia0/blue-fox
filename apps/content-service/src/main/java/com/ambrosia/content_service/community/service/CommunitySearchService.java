package com.ambrosia.content_service.community.service;

import java.util.Optional;

import com.ambrosia.content_service.community.model.entity.CommunityProjection;

public interface CommunitySearchService {
    Optional<CommunityProjection> findById(Long communityId);
}
