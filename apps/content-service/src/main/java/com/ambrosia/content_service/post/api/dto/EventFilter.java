package com.ambrosia.content_service.post.api.dto;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Sort.Direction;

import lombok.Builder;

@Builder
public record EventFilter(
    UUID authorId,
    String searchString,
    Long communityId,
    List<String> tags,
    SearchType searchType,
    Long lastSeenId,
    Long lastSeenInstant,
    Long lastSeenLikeCount,
    Float lastScore,
    Direction direction,
    SortField sortField
) {
    public EventFilter{
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

    public enum SearchType {
        POPULAR,
        RELEVANCY,
        LATEST,
        BEST,
        PERSONALIZED;
    }

    public enum SortField{
        SCORE,
        DATE;
    }
}
