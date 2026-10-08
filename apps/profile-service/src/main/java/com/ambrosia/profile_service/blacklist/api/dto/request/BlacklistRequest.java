package com.ambrosia.profile_service.blacklist.api.dto.request;

import jakarta.validation.constraints.Size;

public record BlacklistRequest(
    @Size(max = 64) String reason
) {}
