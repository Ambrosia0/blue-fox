package com.ambrosia.library_core.breaker.impl;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.core.retry.RetryPolicy;
import org.springframework.core.retry.RetryTemplate;

public class RetrySimpleCircuitBreaker extends SimpleCircuitBreaker{
    private RetryTemplate retryTemplate;
    
    public RetrySimpleCircuitBreaker(
        String id,
        int errorThreshold,
        Duration openTimeout,
        int successThreshold,
        int concurrentHalfOpenRequests,
        RetryPolicy retryPolicy
    ){
        this.retryTemplate = new RetryTemplate(retryPolicy);
        super(
            id,
            errorThreshold,
            openTimeout,
            successThreshold,
            concurrentHalfOpenRequests
        );
    }

    @Override
    public <T> T execute(Supplier<T> supplier, Function<Throwable, T> fallback) {
        return retryTemplate.invoke(() -> super.execute(supplier, fallback));
    }
}
