package com.ambrosia.content_service.follow.api.dto;

import java.util.List;
import java.util.UUID;

public record FollowSnapshot(
    List<UUID> followedUsers,
    List<Long> followedCommunities
) {}
