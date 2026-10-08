package com.ambrosia.community_service.community.api.dto.request;

import java.util.Set;
import java.util.UUID;

import com.ambrosia.community_service.community.utils.ScopeEnum;

import jakarta.validation.constraints.NotNull;

public record ScopePair(
    @NotNull UUID userId,
    @NotNull Set<ScopeEnum> scopes
) {}
