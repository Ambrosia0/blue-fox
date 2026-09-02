package com.ambrosia.content_service.metrics;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostEvent;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Counter;

@Component
public class PostMetric {
    private Counter createCounter;

    private Counter deleteCounter;

    public PostMetric(MeterRegistry meterRegistry){
        this.createCounter = meterRegistry.counter(
            "posts.created",
            Tags.empty()
        );

        this.deleteCounter = meterRegistry.counter(
            "posts.deleted",
            Tags.empty()
        );
    }

    @Async
    @EventListener
    public void on(PostEvent postEvent){
        switch(postEvent.getEventCase()){
            case CREATED -> createCounter.increment();
            case DELETED -> deleteCounter.increment();
            default ->{}
        }
    }
}
