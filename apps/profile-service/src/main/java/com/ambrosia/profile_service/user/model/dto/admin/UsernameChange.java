package com.ambrosia.profile_service.user.model.dto.admin;

import java.time.Instant;
import java.util.UUID;

public record UsernameChange(
    UUID id,
    String username,
    Instant changedAt
) {}
