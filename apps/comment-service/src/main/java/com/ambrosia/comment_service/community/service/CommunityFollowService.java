package com.ambrosia.comment_service.community.service;

import java.util.UUID;

public interface CommunityFollowService {
    boolean isUserFollowed(UUID userId, Long communityId);
}
