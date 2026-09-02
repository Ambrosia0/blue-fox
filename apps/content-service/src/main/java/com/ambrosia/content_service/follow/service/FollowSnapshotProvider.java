package com.ambrosia.content_service.follow.service;

import java.util.UUID;

import com.ambrosia.content_service.follow.model.dto.FollowSnapshot;

public interface FollowSnapshotProvider {
    FollowSnapshot get(UUID userId);
}
