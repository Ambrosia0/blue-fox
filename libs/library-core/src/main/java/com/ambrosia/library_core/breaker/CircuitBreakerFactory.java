package com.ambrosia.library_core.breaker;

public interface CircuitBreakerFactory {
    CircuitBreaker create(String id);
}
