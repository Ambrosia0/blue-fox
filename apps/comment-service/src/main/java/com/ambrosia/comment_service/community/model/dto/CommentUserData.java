package com.ambrosia.comment_service.community.model.dto;

import java.util.UUID;

public record CommentUserData(
    boolean isModerator,
    UUID userId
) {}
