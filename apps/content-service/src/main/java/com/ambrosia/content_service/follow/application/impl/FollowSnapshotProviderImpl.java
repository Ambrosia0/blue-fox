package com.ambrosia.content_service.follow.application.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ambrosia.content_service.community.repository.CommunityFollowProjectionRepository;
import com.ambrosia.content_service.follow.api.dto.FollowSnapshot;
import com.ambrosia.content_service.follow.application.FollowSnapshotProvider;
import com.ambrosia.content_service.follow.domain.repository.UserFollowRepository;

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
