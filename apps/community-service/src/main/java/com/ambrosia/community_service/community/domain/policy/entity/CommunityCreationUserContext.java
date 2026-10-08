package com.ambrosia.community_service.community.domain.policy.entity;

import com.ambrosia.library_policy.policy.registry.PolicyData;

public record CommunityCreationUserContext(
    int ownedCommunities
) implements PolicyData{}
