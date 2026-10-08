package com.ambrosia.community_service.community.application.policy;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import com.ambrosia.community_service.community.domain.policy.entity.CommunityCreationUserContext;
import com.ambrosia.community_service.community.domain.policy.entity.CommunityEditUserContext;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityFollowUserContext;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityModeratorUserContext;
import com.ambrosia.community_service.core.domain.policy.entity.CommunityUserContext;

import jakarta.annotation.Nullable;

public interface CommunityUserDataRepository {
    Optional<CommunityUserContext> loadCommunityUserData(UUID userId, String slug);
    Optional<CommunityUserContext> loadCommunityUserData(UUID userId, Long communityId);
    Optional<CommunityFollowUserContext> loadFollowUserData(UUID userId, Long communityId);
    Optional<CommunityModeratorUserContext> loadModeratorUserData(UUID userId, Long communityId, @Nullable UUID targetUser);
    CommunityCreationUserContext loadCreationUserData(UUID userId);
    Optional<CommunityEditUserContext> loadEditUserData(UUID userId, Long communityId, @Nullable Set<UUID> targetUsers);
}
