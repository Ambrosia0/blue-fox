package com.ambrosia.content_service.follow.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.follow.model.entity.keys.CommunityFollowKey;
import com.ambrosia.content_service.follow.repository.CommunityFollowProjectionRepository;
import com.ambrosia.content_service.follow.service.CommunityFollowProjectionService;

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
