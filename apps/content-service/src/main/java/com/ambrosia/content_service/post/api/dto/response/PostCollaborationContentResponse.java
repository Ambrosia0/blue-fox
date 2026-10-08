package com.ambrosia.content_service.post.api.dto.response;

import java.time.Instant;
import java.util.List;

public record PostCollaborationContentResponse(
    Long id,
    UserResponse author,
    String title,
    String content,
    List<String> tags,
    CommunityResponse community,
    List<UserResponse> collaborators,
    List<String> attachmentIds,
    Instant updatedAt
) {}
