package com.ambrosia.content_service.post.model.dto.response;

import java.time.Instant;
import java.util.UUID;

public record PostEditorViewResponse(
    long id,
    UUID authorId,
    String title,
    Instant updatedAt
) {}
