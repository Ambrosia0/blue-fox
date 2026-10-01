package com.ambrosia.community_service.follow.service;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import com.ambrosia.community_service.follow.model.dto.response.CommunityFollowResponse;

public interface CommunityFollowQueryService {
    Slice<CommunityFollowResponse> getFollows(UUID userId, Pageable pageable);
}
