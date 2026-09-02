package com.ambrosia.content_service.post.service;

import java.util.Collection;

import com.ambrosia.content_service.kafka_events.AggregatedPreviewEvent;
import com.ambrosia.content_service.kafka_events.AggregatedViewEvent;
import com.ambrosia.content_service.kafka_events.PostDelta;

public interface PostInternalService {
    void incrementViewCount(Collection<AggregatedViewEvent> toIncrement);
    void incrementPreviewCount(Collection<AggregatedPreviewEvent> toIncrement);
    void incrementCommentCount(Collection<PostDelta> toIncrement);
}
