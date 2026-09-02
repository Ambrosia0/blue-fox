package com.ambrosia.comment_service.grpc;

import java.util.UUID;

public interface CommunityService {
    boolean isUserAllowed(UUID userId, String scope, long communityId);
}
