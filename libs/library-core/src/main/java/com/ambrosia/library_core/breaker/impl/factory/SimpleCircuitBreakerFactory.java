package com.ambrosia.library_core.breaker.impl.factory;

import java.time.Duration;

import org.springframework.core.retry.RetryPolicy;

import com.ambrosia.library_core.breaker.CircuitBreaker;
import com.ambrosia.library_core.breaker.CircuitBreakerFactory;
import com.ambrosia.library_core.breaker.impl.RetrySimpleCircuitBreaker;
import com.ambrosia.library_core.breaker.impl.SimpleCircuitBreaker;


public class SimpleCircuitBreakerFactory implements CircuitBreakerFactory{
    private Builder builder;

    private SimpleCircuitBreakerFactory(Builder builder){
        this.builder = builder;
    }

    @Override
    public CircuitBreaker create(String id) {
        if(builder.retryPolicy == null)
            return new SimpleCircuitBreaker(
                id,
                builder.errorThreshold,
                builder.openTimeout,
                builder.successThreshold,
                builder.concurrentHalfOpenRequests
        );
        return new RetrySimpleCircuitBreaker(
            id,
            builder.errorThreshold,
            builder.openTimeout,
            builder.successThreshold,
            builder.concurrentHalfOpenRequests,
            builder.retryPolicy
        );
    }

    public static Builder builder(){
        return new Builder();
    }

    public static class Builder{
        private int errorThreshold = 5;
        private Duration openTimeout = Duration.ofMinutes(1);
        private int successThreshold = 1;
        private int concurrentHalfOpenRequests = 3;
        private RetryPolicy retryPolicy = null;

        public Builder errorThreshold(int errorThreshold){
            this.errorThreshold = errorThreshold;
            return this;
        }

        public Builder openTimeout(Duration openTimeout){
            if(openTimeout == null)
                throw new IllegalArgumentException();
            this.openTimeout = openTimeout;
            return this;
        }

        public Builder successThreshold(int successThreshold){
            this.successThreshold = successThreshold;
            return this;
        }

        public Builder concurrentHalfOpenRequests(int concurrentRequests){
            this.concurrentHalfOpenRequests = concurrentRequests;
            return this;
        }

        public Builder retryPolicy(RetryPolicy retryPolicy){
            this.retryPolicy = retryPolicy;
            return this;
        }

        public SimpleCircuitBreakerFactory build(){
            return new SimpleCircuitBreakerFactory(this);
        }
    }
}
