package com.ambrosia.content_service.follow.application;

import java.util.UUID;

import com.ambrosia.content_service.follow.api.dto.FollowSnapshot;

public interface FollowSnapshotProvider {
    FollowSnapshot get(UUID userId);
}
