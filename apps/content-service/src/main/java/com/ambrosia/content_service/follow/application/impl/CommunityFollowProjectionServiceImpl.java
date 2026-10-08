package com.ambrosia.content_service.follow.application.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.repository.CommunityFollowProjectionRepository;
import com.ambrosia.content_service.follow.application.CommunityFollowProjectionService;
import com.ambrosia.content_service.follow.infrastructure.entity.keys.CommunityFollowKey;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommunityFollowProjectionServiceImpl implements CommunityFollowProjectionService{
    private final CommunityFollowProjectionRepository communityFollowProjectionRepository;
    
    @Override
    public boolean isFollowed(long communityId, UUID userId) {
        return communityFollowProjectionRepository.existsById(
            CommunityFollowKey.create(userId, communityId));
    }

    @Override
    public boolean isFollowedOnPrivateOrDoesntPrivate(long communityId, UUID userId) {
        return communityFollowProjectionRepository.followExistsOnPrivateOrDoesntPrivate(communityId, userId);
    }
}
