package com.ambrosia.community_service.follow.service;

import java.util.UUID;

public interface CommunityFollowRequestService {
    void createFollowRequest(UUID userId, Long communityId);
}
