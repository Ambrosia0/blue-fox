package com.ambrosia.community_service.follow.repository.custom;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ambrosia.community_service.follow.model.entity.CommunityFollowRequest;


public interface CustomCommunityFollowRequestRepository {
    List<CommunityFollowRequest> findRequests(long communityId, Pageable pageable);
}
