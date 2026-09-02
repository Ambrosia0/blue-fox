package com.ambrosia.profile_service.metrics;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.ambrosia.profile_service.kafka_events.UserEvent;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Component
public class UserMetric {
    private final Counter counterMetric;

    public UserMetric(MeterRegistry meterRegistry){
        this.counterMetric = meterRegistry.counter(
            "users.created", 
            Tags.empty()
        );
    }

    @Async
    @EventListener
    void on(UserEvent userEvent){
        counterMetric.increment();
    }
}
