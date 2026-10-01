package com.ambrosia.community_service.community.model.dto.response;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record CommunityEditResponse(
    String displayedName,
    String description,
    boolean isPrivate,
    List<String> rules,
    Set<String> tags,
    UUID ownerId,
    Long version
) {}
