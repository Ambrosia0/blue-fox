package com.ambrosia.outbox;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractOutboxRelay<T> {
    
    @Scheduled(fixedDelayString = "${app.outbox.elasticsearch.poll-interval:5000}")
    public void flush(){
        var batch = fetchBatch();
        processBatch(batch);
    }

    protected abstract List<T> fetchBatch();
    protected abstract void processBatch(List<T> batch);
}
