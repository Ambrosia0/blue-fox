package com.ambrosia.profile_service.user.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record ProfileUserData(
    boolean isBlacklisted,

    @JsonInclude(value = Include.NON_NULL)
    String reason
) {}
