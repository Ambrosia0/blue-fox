package com.ambrosia.community_service.community.model.dto.request;

import java.util.List;

import org.springframework.data.domain.Sort.Direction;

import org.hibernate.validator.constraints.UniqueElements;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CommunityEventFilter(
    @Size(min = 3, max = 32) String searchString,
    Long lastSeenId,
    Float lastSeenScore,
    Long lastSeenInstant,
    @UniqueElements @Size(max = 3) List<@Pattern(regexp = "^#?[A-Za-z][A-Za-z0-9_-]*$") String> tags,
    Direction direction
) {
    public CommunityEventFilter{
        if(direction == null)
            direction = Direction.DESC;
    }
}
