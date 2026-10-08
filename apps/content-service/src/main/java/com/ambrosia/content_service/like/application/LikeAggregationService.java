package com.ambrosia.content_service.like.application;

import java.util.UUID;

public interface LikeAggregationService {
    void add(Long postId, UUID userId, boolean isIncrement);
    void remove(Long postId, UUID userId, boolean isIncrement);
}
