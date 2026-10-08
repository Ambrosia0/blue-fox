package com.ambrosia.community_service.follow.api.dto.request;

import java.time.Instant;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;

@Builder 
public record FollowFilter(
    Type type,
    String searchString,
    Cursor cursor,
    Sort.Direction direction
) {
    public FollowFilter{
        if(type == null)
            type = Type.REQUESTED;

        if(direction == null)
            direction = Direction.DESC;
    }

    public enum Type{
        FOLLOWED,
        REQUESTED;
    }

    public record Cursor(
        Long lastSeenId,
        Instant lastInstant
    ){}
}
