package com.ambrosia.library_core.breaker.impl;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ambrosia.library_core.breaker.CircuitBreaker;
import com.ambrosia.library_core.breaker.util.CircuitState;
import com.ambrosia.library_core.breaker.util.CircuitState.State;


public class SimpleCircuitBreaker implements CircuitBreaker{
    protected static final Logger log = LoggerFactory.getLogger(RestClientCircuitBreaker.class);

    protected final String id;

    protected final int errorThreshold;
    protected final int successThreshold;

    protected final long openTimeout;

    protected AtomicReference<CircuitState> circuitState = new AtomicReference<CircuitState>(
        new CircuitState(State.CLOSE, 0L)
    );

    protected AtomicInteger success = new AtomicInteger(0);
    protected AtomicInteger errors = new AtomicInteger(0);

    protected Semaphore semaphore;

    public SimpleCircuitBreaker(
        String id,
        int errorThreshold,
        Duration openTimeout,
        int successThreshold,
        int concurrentHalfOpenRequests
        ){
        this.id = id;
        this.errorThreshold = errorThreshold;
        this.successThreshold = successThreshold;
        this.openTimeout = openTimeout.toMillis();
        this.semaphore = new Semaphore(concurrentHalfOpenRequests);
        log.info("Created circuit-breaker id={}", id);
    }
    

    @Override
    public <T> T execute(Supplier<T> supplier, Function<Throwable, T> fallback) {
        if(circuitState.get().state() == State.OPEN){
            if(!isTimeoutExpired())
                return fallback.apply(null);
            if(!tryHalfOpen())
                return fallback.apply(null);
            else
                log.info("Half-opened circuit breaker {} ", id);
        }
        if(circuitState.get().state() == State.HALF_OPEN){
            if(!semaphore.tryAcquire())
                return fallback.apply(null);
            try {
                var res = supplier.get();
                if(incrementSuccessCount() >= successThreshold)
                    if(tryClose()){
                        resetSuccessCounter();
                        log.info("Closed circuit breaker {} ", id);
                    }
                return res;
            } catch (Exception e) {
                if(tryOpen()){
                    log.info("Opened circuit breaker {}", id);
                }
                return fallback.apply(null);
            } finally {
                semaphore.release();
            }
        }

        try {
            return supplier.get();
        } catch (Exception e) {
            if(incrementErrorCount() >= errorThreshold){
                if(tryOpen())
                    resetErrorCounter();
            }
            return fallback.apply(null);
        }
    }

    protected boolean isTimeoutExpired(){
        return (System.currentTimeMillis() - circuitState.get().openTimeMillis()) > openTimeout;
    }

    protected boolean tryOpen(){
        var cur = circuitState.get();
        return circuitState.compareAndSet(
            cur,
            new CircuitState(
                State.OPEN,
                System.currentTimeMillis()
            )
        );
    }

    protected boolean tryHalfOpen(){
        var cur = circuitState.get();
        return circuitState.compareAndSet(
            cur, 
            new CircuitState(
                State.HALF_OPEN,
                System.currentTimeMillis()
            )
        );
    }

    protected boolean tryClose(){
        var cur = circuitState.get();
        return circuitState.compareAndSet(
            cur, 
            new CircuitState(
                State.CLOSE,
                System.currentTimeMillis()
            )
        );
    }

    protected int incrementErrorCount(){
        return errors.incrementAndGet();
    }

    protected int incrementSuccessCount(){
        return success.incrementAndGet();
    }

    protected void resetSuccessCounter(){
        success.set(0);
    }

    protected void resetErrorCounter(){
        errors.set(0);
    }
}
