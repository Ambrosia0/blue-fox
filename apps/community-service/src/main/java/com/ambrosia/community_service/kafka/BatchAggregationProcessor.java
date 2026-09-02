package com.ambrosia.community_service.kafka;

import java.time.Duration;

import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

public class BatchAggregationProcessor<K> implements Processor<K, Integer, K, Integer>{
    private final String storeName;
    private KeyValueStore<K, Integer> keyValueStore;
    private ProcessorContext<K, Integer> context;

    public BatchAggregationProcessor(String storeName){
        this.storeName = storeName;
    }

    @Override
    public void init(ProcessorContext<K, Integer> context) {
        this.keyValueStore = context.getStateStore(storeName);
        this.context = context;
        context.schedule(
            Duration.ofSeconds(20),
            PunctuationType.WALL_CLOCK_TIME,
            this::flush
        );
    }

    @Override
    public void process(Record<K, Integer> record) {
        var value = keyValueStore.get(record.key());
        keyValueStore.put(
            record.key(), 
            value != null? value + record.value(): record.value()
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
