package com.ambrosia.library_core.autoconfigure;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.ambrosia.library_core.breaker.CircuitBreakerFactory;
import com.ambrosia.library_core.breaker.impl.factory.RestClientCircuitBreakerFactory;
import com.ambrosia.library_core.breaker.impl.factory.SimpleCircuitBreakerFactory;
import com.ambrosia.library_core.breaker.util.CircuitBreakerConfiguration;


@AutoConfiguration
@EnableConfigurationProperties(CircuitBreakerConfiguration.class)
@ConditionalOnProperty(
    prefix = "app.circuit-breaker",
    name = "enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class CircuitBreakerAutoConfiguration {
    
    @Bean("simpleCircuitBreakerFactory")
    @ConditionalOnMissingBean
    CircuitBreakerFactory simpleCircuitBreakerFactory(
            CircuitBreakerConfiguration circuitBreakerConfiguration){
        return SimpleCircuitBreakerFactory.builder()
                .errorThreshold(circuitBreakerConfiguration.getErrorThreshold())
                .concurrentHalfOpenRequests(circuitBreakerConfiguration.getConcurrentHalfOpenRequests())
                .openTimeout(circuitBreakerConfiguration.getOpenTimeout())
                .successThreshold(circuitBreakerConfiguration.getSuccessThreshold())
                .build();
    }

    @Bean("restClientCircuitBreakerFactory")
    @ConditionalOnMissingBean
    CircuitBreakerFactory restClientCircuitBreakerFactory(
            CircuitBreakerConfiguration circuitBreakerConfiguration){
        return RestClientCircuitBreakerFactory.builder()
                .errorThreshold(circuitBreakerConfiguration.getErrorThreshold())
                .concurrentHalfOpenRequests(circuitBreakerConfiguration.getConcurrentHalfOpenRequests())
                .openTimeout(circuitBreakerConfiguration.getOpenTimeout())
                .successThreshold(circuitBreakerConfiguration.getSuccessThreshold())
                .build();
    }
}