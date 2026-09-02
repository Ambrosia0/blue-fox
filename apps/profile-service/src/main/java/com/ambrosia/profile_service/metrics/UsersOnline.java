package com.ambrosia.profile_service.metrics;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.KafkaStreams.State;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;

@Component
public class UsersOnline {
    public UsersOnline(MeterRegistry meterRegistry, StreamsBuilderFactoryBean factoryBean){
        Gauge.builder(
            "users.online",
            factoryBean,
            online -> {
                var streams = factoryBean.getKafkaStreams();
                return (streams != null && streams.state() == State.CREATED)?
                    getUsersOnline(streams):
                    0;
            }
        ).register(meterRegistry);
    }

    private long getUsersOnline(KafkaStreams kafkaStreams){
        var store = kafkaStreams.store(
            StoreQueryParameters.fromNameAndType(
                "users-online-store",
                QueryableStoreTypes.keyValueStore()
            ));
        var active = (Long)store.get("active-users");
        return active != null?
            active:
            0;
    }
}
