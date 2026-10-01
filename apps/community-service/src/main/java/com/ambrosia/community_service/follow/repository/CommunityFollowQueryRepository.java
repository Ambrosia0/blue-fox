package com.ambrosia.community_service.follow.repository;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.model.dto.response.CommunityFollowResponse;

public interface CommunityFollowQueryRepository {
    Slice<CommunityFollowResponse> findByUserId(UUID userId, Pageable pageable);
}
