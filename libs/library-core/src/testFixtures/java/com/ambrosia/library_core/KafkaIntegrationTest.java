package com.ambrosia.library_core;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.kafka.KafkaContainer;

@TestConfiguration(proxyBeanMethods = false)
public class KafkaIntegrationTest {
    // static Network network = Network.newNetwork();

    // @Container
    // static final KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0")
    //     .withNetworkAliases("kafka-broker")
    //     .withListener("kafka-broker:19092")
    //     .withNetwork(network);

    static final KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0");

    static public void registerProperties(DynamicPropertyRegistry registry){
        kafka.start();

        registry.add("KAFKA_BOOTSTRAP_SERVERS", () -> kafka.getBootstrapServers());
    }
}
