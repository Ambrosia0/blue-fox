package com.ambrosia.library_core;

import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.Network;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.kafka.KafkaContainer;

import com.ambrosia.library_core.dto.Topics;

import dasniko.testcontainers.keycloak.KeycloakContainer;

@TestConfiguration(proxyBeanMethods = false)
public class KeycloakIntegrationTest {
    static private Network network = Network.newNetwork();

    @Container
    static final KafkaContainer kafka = new KafkaContainer("apache/kafka-native:3.8.0")
        .withNetworkAliases("kafka-broker")
        .withListener("kafka-broker:19092")
        .withNetwork(network);

    @Container
    static final KeycloakContainer keycloak = new KeycloakContainer("ghcr.io/ambrosia0/keycloak-kafka:v1")
        .withImagePullPolicy((_) -> false)
        .withAdminUsername("admin")
        .withAdminPassword("admin")
        .withRealmImportFile("blog-realm.json")
        .withReuse(false)
        .withNetwork(network);
    
        
    static public void registerProperties(DynamicPropertyRegistry registry){
        kafka.start();

        keycloak
            .withExtraHost("host.testcontainers.internal", "host-gateway") 
            .withEnv(Map.of(
                "KC_HEALTH_ENABLED", "true",
                "KAFKA_BOOTSTRAP_SERVERS", kafka.getNetworkAliases().getFirst()+":19092",
                "KAFKA_TOPIC", Topics.KEYCLOAK_EVENT,
                "KAFKA_EVENTS", "REGISTER,DELETE_ACCOUNT",
                "KAFKA_CLIENT_ID", "keycloak-client",
                "KAFKA_ADMIN_TOPIC", Topics.KEYCLOAK_EVENT_ADMIN
            ))
        .start();

        registry.add("OIDC_ISSUER_URL", () -> keycloak.getAuthServerUrl());
        registry.add("KC_BASE_URL", () -> keycloak.getAuthServerUrl());
        registry.add("KC_REALM", () -> "blog");
        registry.add("KC_CLIENT_ID", () -> "profile-service");
        registry.add("KC_SECRET", () -> "XWaoMLR9qctOSvsY1EsHUo8WjYDIOz2o");
        registry.add("KAFKA_BOOTSTRAP_SERVERS", () -> kafka.getBootstrapServers());
    }
}
