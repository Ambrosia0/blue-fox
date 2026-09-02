package com.ambrosia.profile_service.blacklist.model.dto.request;

import jakarta.validation.constraints.Size;

public record BlacklistRequest(
    @Size(max = 64) String reason
) {}
