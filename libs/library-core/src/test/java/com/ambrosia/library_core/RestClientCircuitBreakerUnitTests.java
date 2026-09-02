package com.ambrosia.library_core;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.charset.Charset;
import java.time.Duration;
import java.util.function.Function;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import com.ambrosia.library_core.breaker.CircuitBreakerFactory;
import com.ambrosia.library_core.breaker.impl.factory.RestClientCircuitBreakerFactory;

public class RestClientCircuitBreakerUnitTests {
    int openTimeoutDuration = 5;
    CircuitBreakerFactory factory = RestClientCircuitBreakerFactory
        .builder()
        .errorThreshold(1)
        .openTimeout(Duration.ofSeconds(openTimeoutDuration))
        .successThreshold(1)
        .concurrentHalfOpenRequests(1)
        .build();

    @Test
    void shouldNotOpenBreaker(){
        var breaker = factory.create("test-breaker");
        assertThrows(
            RestClientResponseException.class,
            () -> breaker.execute(
                () -> {
                    throw new RestClientResponseException(
                        "Test exception", 
                        HttpStatus.BAD_REQUEST, 
                        "Test", 
                        HttpHeaders.EMPTY, 
                        "test".getBytes(), 
                        Charset.defaultCharset()
                    );
                },
                integerFallback()
            )
        );
        assertDoesNotThrow(
            () -> breaker.execute(
                successfullProcess(),
                throwable -> {
                    throw new RuntimeException();
                }
            )
        );
    }

    @Test
    void shouldOpenBreaker() throws Exception{
        var breaker = factory.create("test-breaker");
        assertThrows(
            RuntimeException.class,
            () -> breaker.execute(
                failedProcess(), 
                throwable -> {
                    throw new RuntimeException();
                }
            )
        );
        assertThrows(
            RuntimeException.class,
            () -> breaker.execute(
                process(
                    ThreadLocal.withInitial(() -> 0)),
                    throwable -> {
                        throw new RuntimeException();
                    }
                )
        );
    }

    @Test
    void shouldCloseBreakerAfterFailure() throws Exception{
        var breaker = factory.create("test-breaker");
        assertThrows(
            RuntimeException.class,
            () -> breaker.execute(
                failedProcess(), 
                throwable -> {
                    throw new RuntimeException();
                }
            )
        );
        Thread.sleep(Duration.ofSeconds(openTimeoutDuration));
        assertEquals(
            1,
            breaker.execute(
                process(ThreadLocal.withInitial(() -> 0)),
                throwable -> {
                    throw new RuntimeException();
                }
            )
        );
        assertEquals(
            1,
            breaker.execute(
                process(ThreadLocal.withInitial(() -> 0)),
                throwable -> {
                    throw new RuntimeException();
                }
            )
        );
    }

    @Test
    void shouldThrowRuntimeExceptionOnConcurrentRequestCountExceed() throws InterruptedException{
        var breaker = factory.create("test-breaker");
        assertThrows(
            RuntimeException.class,
            () -> breaker.execute(
                failedProcess(),
                throwable -> { throw new RuntimeException(); }
            )
        );
        Thread.sleep(Duration.ofSeconds(openTimeoutDuration).plusSeconds(1));
        Thread.startVirtualThread(() -> {
            breaker.execute(
                () -> {
                    try {
                        Thread.sleep(3000);
                        return null;
                    } catch (Exception e) {
                        throw new RuntimeException();
                    }
                },
                throwable -> {
                    throw new RuntimeException();
                }
            );
        });
        Thread.sleep(1000);
        assertThrows(
            RuntimeException.class,
            () -> breaker.execute(
                () -> {
                    return 1;
                },
                throwable -> {
                    throw new RuntimeException();
                }
            )
        );
    }

    private Supplier<Void> failedProcess(){
        return () -> {
            throw new ResourceAccessException("Test Exception");
        };
    }

    private Supplier<Integer> successfullProcess(){
        return () -> {
            return 1;
        };
    }

    private Function<Throwable, Integer> integerFallback(){
        return throwable ->{
            return 1;
        };
    }

    private Supplier<?> process(ThreadLocal<Integer> local){
        return () -> {
            var localVal = local.get();
            local.set(localVal + 1);
            return local.get();
        };
    }
}
