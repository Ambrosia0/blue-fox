package com.ambrosia.content_service.search.model.dto;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;

@Builder
public record EventFilter(
    UUID authorId,
    String searchString,
    Long lastSeenId,
    Long lastSeenInstant,
    Long lastSeenLikeCount,
    Long communityId,
    List<String> tags,
    SearchType searchType,
    Float lastScore,
    Boolean visible,
    Direction direction,
    SortField sortField
) {
    public EventFilter{
        visible = true;
        if(searchType == null){
            searchType = SearchType.LATEST;
        }
        if(direction == null){
            direction = Direction.DESC;
        }
        if(searchType == SearchType.PERSONALIZED && sortField == null){
            sortField = SortField.SCORE;
        }
    }

    public enum SortField{
        SCORE,
        DATE;
    }
}
