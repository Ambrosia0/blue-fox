package com.ambrosia.community_service.community.application.query.model;

import java.time.Instant;

public record CommunityPreview(
    long id,
    String slug,
    String displayedName,

    long followCount,

    String avatarId,

    String[] tags,

    Instant createdAt
) {
}
