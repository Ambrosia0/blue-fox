package com.ambrosia.community_service.community.application;

import java.time.Instant;
import java.util.UUID;

import com.ambrosia.library_policy.policy.Actor;

public interface CommunityModeratorService {
    void banUser(long communityId, Actor actor, UUID userToBan, Instant beforeDate);
    void unbanUser(long communityId, Actor actor, UUID userToUnban);
}
