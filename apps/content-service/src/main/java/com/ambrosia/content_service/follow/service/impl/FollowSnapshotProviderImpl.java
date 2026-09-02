package com.ambrosia.content_service.follow.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.follow.model.dto.FollowSnapshot;
import com.ambrosia.content_service.follow.repository.CommunityFollowProjectionRepository;
import com.ambrosia.content_service.follow.repository.UserFollowRepository;
import com.ambrosia.content_service.follow.service.FollowSnapshotProvider;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FollowSnapshotProviderImpl implements FollowSnapshotProvider{
    private final UserFollowRepository userFollowRepository;

    private final CommunityFollowProjectionRepository communityFollowProjectionRepository;

    @Override
    public FollowSnapshot get(UUID userId) {
        var userFollows = userFollowRepository.findFollowsByUserId(userId);
        var communityFollows = communityFollowProjectionRepository.findFollowedByUserId(userId);
        return new FollowSnapshot(
            userFollows,
            communityFollows
        );
    }
}
