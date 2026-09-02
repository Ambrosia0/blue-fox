package com.ambrosia.content_service.metrics;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ambrosia.content_service.kafka_events.PostViewEvent;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Counter;

@Component
public class PostViewMetric {
    public Counter counter;
    
    public PostViewMetric(MeterRegistry meterRegistry){
        this.counter = meterRegistry.counter(
            "posts.views",
            Tags.empty()
        );
    }

    @Async
    @EventListener
    public void on(PostViewEvent viewEvent){
        counter.increment();
    }
}
