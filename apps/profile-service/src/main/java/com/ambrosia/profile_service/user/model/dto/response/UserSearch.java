package com.ambrosia.profile_service.user.model.dto.response;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public record UserSearch(
    UUID id,
    String username,

    String firstName,
    String lastName,

    @JsonInclude(value = Include.NON_NULL)
    String avatarId
) {}
