package com.ambrosia.comment_service.comment.model.dto;

import java.time.Instant;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;

@Builder
public record EventFilter(
    SortField sortField,
    Long lastSeenId,
    Instant lastSeenInstant,
    Integer lastSeenCount,
    Direction direction,
    boolean visible
) {
    
    public enum SortField{
        DATE,
        LIKES,
        HOT;
    }
}
