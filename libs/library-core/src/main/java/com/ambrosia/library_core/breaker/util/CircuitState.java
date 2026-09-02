package com.ambrosia.library_core.breaker.util;

public record CircuitState(
    State state,
    long openTimeMillis){
    public enum State{
        OPEN, CLOSE, HALF_OPEN;
    }
}