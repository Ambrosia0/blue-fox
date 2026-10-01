package com.ambrosia.community_service.community.service;

import java.time.Instant;
import java.util.UUID;

public interface CommunityModeratorService {
    void banUser(long communityId, UUID requestingUser, UUID userToBan, Instant beforeDate);
    void unbanUser(long communityId, UUID requestingUser, UUID userToUnban);
}
