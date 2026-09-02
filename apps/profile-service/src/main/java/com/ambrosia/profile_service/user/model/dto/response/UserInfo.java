package com.ambrosia.profile_service.user.model.dto.response;

import java.util.UUID;

import com.ambrosia.profile_service.user.utils.Status;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record UserInfo(
    UUID id,
    String username,

    @JsonInclude(value = Include.NON_NULL)
    String avatarId,

    @JsonInclude(value = Include.NON_NULL)
    Status status
) {}
