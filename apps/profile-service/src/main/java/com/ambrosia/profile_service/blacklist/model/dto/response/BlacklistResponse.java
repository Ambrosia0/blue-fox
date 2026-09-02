package com.ambrosia.profile_service.blacklist.model.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record BlacklistResponse(
    UUID id,
    String username,

    @JsonInclude(value = Include.NON_NULL)
    String avatarId,

    String reason
) {}
