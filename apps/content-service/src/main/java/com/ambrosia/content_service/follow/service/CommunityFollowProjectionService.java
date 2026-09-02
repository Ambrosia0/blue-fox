package com.ambrosia.content_service.follow.service;

import java.util.UUID;

public interface CommunityFollowProjectionService {
    boolean isFollowed(long communityId, UUID userId);
    boolean isFollowedOnPrivateOrDoesntPrivate(long communityId, UUID userId);
}
