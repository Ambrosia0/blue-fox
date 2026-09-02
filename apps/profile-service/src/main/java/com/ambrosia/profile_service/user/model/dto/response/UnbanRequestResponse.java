package com.ambrosia.profile_service.user.model.dto.response;

import java.time.Instant;
import java.util.UUID;

public record UnbanRequestResponse(
    String request,
    Instant createdAt,
    UserInfo user
) {
    public record UserInfo(
        UUID id,
        String username,
        String about,
        String email,
        Instant createdAt
    ){}
}
