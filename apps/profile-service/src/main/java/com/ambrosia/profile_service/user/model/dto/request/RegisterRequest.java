package com.ambrosia.profile_service.user.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @Size(min = 8, max = 64)
    String username,

    @Size(max = 32)
    String firstName,

    @Size(max = 32)
    String lastName,

    @Size(min = 8, max = 64)
    String password,

    @Email
    String email
) {}