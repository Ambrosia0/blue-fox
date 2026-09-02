package com.ambrosia.library_core;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class RedisIntegrationTest {
    static final GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:8.6.1"))
        .withExposedPorts(6379)
        .withReuse(false);

    static public void registerProperties(DynamicPropertyRegistry registry){
        redis.start();
        registry.add("REDIS_URL", () -> "redis://"+redis.getHost()+":"+redis.getMappedPort(6379));
    }
}
