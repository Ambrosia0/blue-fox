package com.ambrosia.community_service.community.model.dto.request;

import java.util.List;
import java.util.UUID;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommunityEdit(
    @Size(max = 3) @UniqueElements List<@Pattern(regexp = "^#?[A-Za-z][A-Za-z0-9_-]*$") String> tags,

    @Size(max = 5) List<@NotBlank @Size(max = 128) String> rules,
    String displayedName,
    String description,
    UUID ownerId
) {}
