package com.ambrosia.content_service.post.model;

import java.util.UUID;

public record DeletionProjection(
    Long id,
    Long communityId,
    UUID authorId,
    Long version
) {}
