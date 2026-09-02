package com.ambrosia.content_service.post.repository.custom;

import java.util.Collection;
import java.util.Map.Entry;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.kafka_events.PostDelta;

public interface CustomPostRepository {
    int incrementAll(Iterable<Entry<Long, Long>> iterable);
    void batchViewIncrement(Collection<AggregatedViewEvent> toIncrement);
    void batchPreviewIncrement(Collection<AggregatedPreviewEvent> toIncrement);
    void batchCommentCountIncrement(Collection<PostDelta> toIncrement);
}
