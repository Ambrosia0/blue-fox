package com.ambrosia.library_core;

import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class PostgresRumIntegrationTest {
    static private GenericContainer<?> postgres = new GenericContainer<>(DockerImageName.parse("ghcr.io/ambrosia0/postgres-18-rum:v1"))
        .withEnv(
            Map.of(
                "POSTGRES_DB", "testdb",
                "POSTGRES_USERNAME", "postgres",
                "POSTGRES_PASSWORD", "1111"
            )
        )
        .withReuse(false)
        .withExposedPorts(5432);

    static public void registerProperties(DynamicPropertyRegistry registry){
        postgres.start();

        registry.add("DB_URL", () -> "jdbc:postgresql://"+postgres.getHost()+":"+ postgres.getMappedPort(5432) + "/testdb");
        registry.add("DB_USER", () -> "postgres");
        registry.add("DB_PASSWORD", () -> "1111");
    }

}
