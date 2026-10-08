package com.ambrosia.community_service.community.api.dto.request;

import java.util.Set;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommunityCreate(
    @NotBlank @Size(min = 6, max = 40) String displayedName,
    @NotBlank @Size(min = 6, max = 32) @Pattern(regexp = "^[a-z0-9_-]$") String slug,
    @NotNull Boolean isPrivate,
    @Size(max = 3) Set<@Size(min = 3, max = 32) @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$") String> tags
) {}
