package com.ambrosia.comment_service.metrics;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ambrosia.comment_service.kafka_events.CommentEvent;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class CommentMetric {
    private Counter commentCreate;
    private Counter commentDelete;

    public CommentMetric(MeterRegistry meterRegistry){
        this.commentCreate = meterRegistry.counter(
            "comment.created",
            Tags.empty()
        );

        this.commentDelete = meterRegistry.counter(
            "comment.deleted",
            Tags.empty()
        );
    }

    @Async
    @EventListener
    public void on(CommentEvent commentEvent){
        switch (commentEvent.getEventCase()) {
            case CREATED -> commentCreate.increment();
            case DELETED -> commentDelete.increment();
            default -> {}
        }
    }
}
