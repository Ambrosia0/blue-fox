package com.ambrosia.library_core.breaker.util;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.circuit-breaker")
public class CircuitBreakerConfiguration {
    /**
     * Error threshold for circuit breaker
     */
    private int errorThreshold = 10;

    /**
     * Error count reset timeout for circuit breaker
     */
    private Duration resetTimeout = Duration.ofMinutes(1);
    
    /**
     * Duration of open state of circuit breaker
     */
    private Duration openTimeout = Duration.ofMinutes(1);

    /**
     * Number of success requests before circuit-breaker should close
     */
    private int successThreshold = 10;

    /**
     * Number of concurrent requests in half-open state of circuit-breaker
     */
    private int concurrentHalfOpenRequests = 5;

    public Integer getErrorThreshold(){
        return this.errorThreshold;
    }

    public Duration getResetTimeout(){
        return this.resetTimeout;
    }

    public Duration getOpenTimeout(){
        return this.openTimeout;
    }

    public Integer getSuccessThreshold(){
        return this.successThreshold;
    }

    public Integer getConcurrentHalfOpenRequests(){
        return this.concurrentHalfOpenRequests;
    }

    public void setErrorThreshold(int errorThreshold){
        this.errorThreshold = errorThreshold;
    }

    public void setResetTimeout(Duration resetTimeout){
        this.resetTimeout = resetTimeout;
    }

    public void setOpenTimeout(Duration openTimeout){
        this.openTimeout = openTimeout;
    }

    public void setSuccessThreshold(int successThreshold){
        this.successThreshold = successThreshold;
    }

    public void setConcurrentHalfOpenRequests(int concurrentHalfOpenRequests){
        this.concurrentHalfOpenRequests = concurrentHalfOpenRequests;
    }
}
