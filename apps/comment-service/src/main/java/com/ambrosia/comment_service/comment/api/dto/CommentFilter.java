package com.ambrosia.comment_service.comment.api.dto;

import java.time.Instant;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;

@Builder 
public record CommentFilter(
    Instant lastSeenInstant,
    Long lastSeenId,
    Direction direction
) {
    public CommentFilter{
        if(direction == null)
            direction = Direction.DESC;
    }
}
