package com.ambrosia.community_service.metrics;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ambrosia.community_service.kafka_events.CommunityEvent;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class CommunityMetric {
    public Counter communityCreated;
    public Counter communityDeleted;

    public CommunityMetric(MeterRegistry meterRegistry){
        this.communityCreated = meterRegistry.counter(
            "community.created",
            Tags.empty()
        );

        this.communityDeleted = meterRegistry.counter(
            "community.deleted",
            Tags.empty()
        );
    }

    @Async
    @EventListener
    public void on(CommunityEvent communityEvent){
        switch (communityEvent.getEventCase()) {
            case CREATE -> communityCreated.increment();
            case DELETE -> communityDeleted.increment();
            default ->{}
        }
    }
}
