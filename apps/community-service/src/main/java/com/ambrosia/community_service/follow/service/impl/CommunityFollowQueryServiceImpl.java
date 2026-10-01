package com.ambrosia.community_service.follow.service.impl;

import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import com.ambrosia.community_service.follow.model.dto.response.CommunityFollowResponse;
import com.ambrosia.community_service.follow.repository.CommunityFollowQueryRepository;
import com.ambrosia.community_service.follow.service.CommunityFollowQueryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class CommunityFollowQueryServiceImpl implements CommunityFollowQueryService{
    private final CommunityFollowQueryRepository communityFollowQueryRepository;
    
    @Override
    public Slice<CommunityFollowResponse> getFollows(UUID userId, Pageable pageable) {
        return communityFollowQueryRepository.findByUserId(userId, pageable);
    }
}
