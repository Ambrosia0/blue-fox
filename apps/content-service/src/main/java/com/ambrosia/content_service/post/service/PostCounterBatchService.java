package com.ambrosia.content_service.post.service;

import java.util.Collection;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.kafka_events.PostDelta;

/**
 * Internal service for batch operations over counters
 * PostCounterIncrementService
 */
public interface PostCounterBatchService {
    /**
     * Increments view counter
     * @param toIncrement events
     */
    void incrementViewCount(Collection<AggregatedViewEvent> toIncrement);

    /**
     * Increments preview counter
     * @param toIncrement events
     */
    void incrementPreviewCount(Collection<AggregatedPreviewEvent> toIncrement);

    /**
     * Increments comment counter
     * @param toIncrement events
     */
    void incrementCommentCount(Collection<PostDelta> toIncrement);
}
