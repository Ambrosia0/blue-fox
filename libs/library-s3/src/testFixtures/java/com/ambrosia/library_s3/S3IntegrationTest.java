package com.ambrosia.library_s3;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;

@TestConfiguration(proxyBeanMethods = false)
public class S3IntegrationTest {
    @Container
    static final GenericContainer<?> rustfs = new GenericContainer<>("rustfs/rustfs:latest")
        .withExposedPorts(9000, 9001)
        .waitingFor(
            Wait.forHttp("/health")
                .forPort(9000)
                .forStatusCode(200)
        );

    static public void registerProperties(DynamicPropertyRegistry registry){
        rustfs.start();
        registry.add("S3_ENDPOINT", () -> "http://"+rustfs.getHost()+":"+rustfs.getMappedPort(9000));
        registry.add("S3_BUCKET", () -> "publicbucket");
        registry.add("S3_ACCESS_KEY", () -> "rustfsadmin");
        registry.add("S3_SECRET_KEY", () -> "rustfsadmin");
    }
}
