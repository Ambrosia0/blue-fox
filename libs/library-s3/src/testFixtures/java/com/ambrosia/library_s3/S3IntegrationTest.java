package com.ambrosia.library_s3;

import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration(proxyBeanMethods = false)
public class S3IntegrationTest {
    @Container
    static final GenericContainer<?> minio = new GenericContainer<>("minio/minio:RELEASE.2025-09-07T16-13-09Z-cpuv1")
        .withCommand("server", "/data")
        .withEnv(Map.of(
            "MINIO_ROOT_USER", "minioadmin",
            "MINIO_ROOT_PASSWORD", "minioadmin"
        ))
        .withExposedPorts(9000, 9001)
        .waitingFor(
            Wait.forHttp("/minio/health/ready")
                .forPort(9000)
                .forStatusCode(200)
        );

    static public void registerProperties(DynamicPropertyRegistry registry){
        minio.start();
        registry.add("S3_ENDPOINT", () -> "http://"+minio.getHost()+":"+minio.getMappedPort(9000));
        registry.add("S3_BUCKET", () -> "publicbucket");
        registry.add("S3_ACCESS_KEY", () -> "minioadmin");
        registry.add("S3_SECRET_KEY", () -> "minioadmin");
    }
}
