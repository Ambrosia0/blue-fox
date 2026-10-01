package com.ambrosia.content_service.post.model.dto.response;

import java.time.Instant;
import java.util.List;

public record PostEditorViewResponse(
    long id,
    UserResponse author,
    String title,
    List<String> tags,
    CommunityResponse communityResponse,
    List<UserResponse> collaborators,
    Instant updatedAt
) {}
