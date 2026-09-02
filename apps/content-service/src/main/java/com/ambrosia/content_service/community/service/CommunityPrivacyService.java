package com.ambrosia.content_service.community.service;

import java.util.Optional;

public interface CommunityPrivacyService {
    Optional<Boolean> isPrivate(long communityId);
}
