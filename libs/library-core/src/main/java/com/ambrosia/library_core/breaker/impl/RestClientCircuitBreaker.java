package com.ambrosia.library_core.breaker.impl;

import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.springframework.util.Assert;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ServerErrorException;

import com.ambrosia.library_core.breaker.util.CircuitState.State;

public class RestClientCircuitBreaker extends SimpleCircuitBreaker{

    public RestClientCircuitBreaker(
        String id,
        int errorThreshold,
        Duration openTimeout,
        int successThreshold,
        int concurrentHalfOpenRequests
    ){
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
        Assert.notNull(supplier, "Supplier must not be null!");
        Assert.notNull(fallback, "Fallback must not be null!");
        
        if(circuitState.get().state() == State.OPEN){
            if(!isTimeoutExpired())
                return fallback.apply(null);
            if(!tryHalfOpen())
                return fallback.apply(null);
            else
                log.info("Half-opened circuit-breaker {}", id);
        }
        if(circuitState.get().state() == State.HALF_OPEN){
            if(!semaphore.tryAcquire())
                return fallback.apply(null);
            try {
                var res = supplier.get();
                if(incrementSuccessCount() >= successThreshold)
                    if(tryClose()){
                        resetSuccessCounter();
                        log.info("Closed circuit-breaker {}", id);
                    }
                return res;
            } 
            catch (ResourceAccessException e) {
                if(tryOpen()){
                    log.info("Opened from half-opened state circuit-breaker {}", id);
                }
                return fallback.apply(null);
            } 
            catch (Exception e){
                if(incrementSuccessCount() >= successThreshold)
                    if(tryClose())
                        resetSuccessCounter();
                throw e;
            }
            finally {
                semaphore.release();
            }
        }

        try {
            return supplier.get();
        } 
        catch (ResourceAccessException | ServerErrorException e) {
            if(incrementErrorCount() >= errorThreshold){
                if(tryOpen()){
                    resetErrorCounter();
                    log.info("Opened circuit-breaker {}", id);
                }
            }
            return fallback.apply(null);
        }
        catch (Exception e){
            throw e;
        }
    }
}
