package com.ambrosia.library_core;

import java.util.List;
import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration(proxyBeanMethods = false)
public class ElasticIntegrationTest {
    @Container
    static final GenericContainer<?> elastic = new GenericContainer<>("elasticsearch:9.3.2")
        .withEnv(Map.of(
            "discovery.type", "single-node",
            "xpack.security.enabled", "false",
            "ES_JAVA_OPTS", "-Xms1g -Xmx1g"
            )
        )
        .withExposedPorts(9200)
        .withReuse(false)
        .waitingFor(Wait.forHttp("/").forStatusCode(200));

    static public void registerProperties(DynamicPropertyRegistry registry){
        elastic.start();
        registry.add("ELASTIC_URIS", () -> List.of("http://" + elastic.getHost() + ":" + elastic.getMappedPort(9200)));
    }
}
