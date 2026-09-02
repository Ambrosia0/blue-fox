package com.ambrosia.content_service.community.service;

import java.util.UUID;

public interface CommunityPermissionService {
    void validatePostCreate(UUID userId, long communityId);
}
