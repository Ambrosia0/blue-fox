package com.ambrosia.comment_service.community.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.comment_service.community.repository.CommunityFollowProjectionRepository;
import com.ambrosia.comment_service.community.service.CommunityFollowService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommunityFollowServiceImpl implements CommunityFollowService{
    private final CommunityFollowProjectionRepository communityFollowProjectionRepository;
    
    @Override
    public boolean isUserFollowed(UUID userId, Long communityId) {
        return communityFollowProjectionRepository.existsByUserIdAndCommunityId(userId, communityId);
    }
}
