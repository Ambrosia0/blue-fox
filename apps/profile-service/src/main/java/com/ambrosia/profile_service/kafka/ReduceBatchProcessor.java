package com.ambrosia.profile_service.kafka;

import java.time.Duration;

import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

public class ReduceBatchProcessor<K, V> implements Processor<K, V, K, V> {
    private final String storeName;
    private KeyValueStore<K, V> keyValueStore;
    private ProcessorContext<K, V> context;

    public ReduceBatchProcessor(String storeName){
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext<K, V> context) {
        this.keyValueStore = context.getStateStore(storeName);
        this.context = context;
        context.schedule(
            Duration.ofSeconds(20),
            PunctuationType.WALL_CLOCK_TIME,
            this::flush
        );
    }

    @Override
    public void process(Record<K, V> record) {
        keyValueStore.put(
            record.key(),
            record.value()
        );
    }

    private void flush(long timestamp){
        try (var it = keyValueStore.all()) {
            while(it.hasNext()){
                var aggregate = it.next();
                context.forward(
                    new Record<>(
                        aggregate.key,
                        aggregate.value,
                        timestamp
                    )
                );
                keyValueStore.delete(aggregate.key);
            }
        }
    }
}
