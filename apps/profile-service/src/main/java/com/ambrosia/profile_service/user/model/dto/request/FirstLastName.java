package com.ambrosia.profile_service.user.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FirstLastName(
    @NotBlank @Size(min = 4, max = 16) String firstName,
    @Size(max = 16) String lastName
) {}
