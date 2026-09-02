package com.ambrosia.library_core.breaker;

import java.util.function.Function;
import java.util.function.Supplier;

public interface CircuitBreaker {
    <T> T execute(Supplier<T> supplier, Function<Throwable, T> fallback);
}
