package com.ambrosia.community_service.community.model.dto.request;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;

import com.ambrosia.community_service.community.utils.ScopeEnum;

import jakarta.validation.constraints.NotNull;

public record ScopePair(
    @NotNull UUID userId,
    @NotNull @UniqueElements(message = "Scopes must be unique") List<ScopeEnum> scopes
) {}
