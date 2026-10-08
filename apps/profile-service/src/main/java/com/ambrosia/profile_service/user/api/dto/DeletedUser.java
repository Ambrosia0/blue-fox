package com.ambrosia.profile_service.user.api.dto;

import java.util.UUID;

public record DeletedUser(
    UUID id,
    Long version
) {}