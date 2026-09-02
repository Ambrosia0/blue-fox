package com.ambrosia.comment_service.kafka;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

public class CommentAggregationProcessor implements Processor<Long, Integer, Long, Integer>{
    private ProcessorContext<Long, Integer> context;
    private static final AtomicLong lastExecutionTime = new AtomicLong(0);
    private static final AtomicLong hearbeatCounter = new AtomicLong(-1);

    @Override
    public void init(ProcessorContext<Long, Integer> context) {
        this.context = context;
        context.schedule(
            Duration.ofSeconds(5),
            PunctuationType.WALL_CLOCK_TIME,
            timestamp -> {
                long currentTime = System.currentTimeMillis();
                long lastTime = lastExecutionTime.get();
                if (currentTime - lastTime >= 1000) {
                    if (lastExecutionTime.compareAndSet(lastTime, currentTime)) {
                        context.forward(
                            new Record<>(
                                hearbeatCounter.getAndDecrement(), 
                                0, 
                                timestamp
                            )
                        );
                    }
                }
            }
        );
    }

    @Override
    public void process(Record<Long, Integer> record) {
        context.forward(record);
    }
}
