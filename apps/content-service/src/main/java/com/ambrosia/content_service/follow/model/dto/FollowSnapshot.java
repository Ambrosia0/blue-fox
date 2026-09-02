package com.ambrosia.content_service.follow.model.dto;

import java.util.List;
import java.util.UUID;

public record FollowSnapshot(
    List<UUID> followedUsers,
    List<Long> followedCommunities
) {}
