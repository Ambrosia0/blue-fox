package com.ambrosia.content_service.community.model.dto;

import java.util.UUID;

public record PostUserData(
    boolean isModerator,
    UUID authorId
) {}
