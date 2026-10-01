package com.ambrosia.community_service.community.model.dto.request;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.ambrosia.community_service.community.utils.ScopeEnum;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder 
public record CommunityEdit(
    @Size(max = 3) Set<@Pattern(regexp = "^#?[A-Za-z][A-Za-z0-9_-]*$") String> tags,

    @Size(max = 5) List<@NotBlank @Size(max = 128) String> rules,
    String displayedName,
    String description,
    UUID ownerId,
    @Size(max = 3) Map<UUID, Set<ScopeEnum>> scopes
) {}
