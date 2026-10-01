package com.ambrosia.content_service.post.model.dto.request;

import java.time.Instant;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;

@Builder 
public record PostEditorFilter(
    Instant lastSeenDate,
    Long lastSeenId,
    SortField sortField,
    Direction direction
) {
    public PostEditorFilter{
        if(sortField == null)
            sortField = SortField.DATE;

        if(direction == null)
            direction = Direction.DESC;
    }
    public enum SortField {
        DATE
    }
}
