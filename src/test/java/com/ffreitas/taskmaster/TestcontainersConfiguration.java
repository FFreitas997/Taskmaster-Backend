package com.ffreitas.taskmaster;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
    }

    @Bean
    public KeycloakContainer keycloak(DynamicPropertyRegistry registry) {
        var keycloak = new KeycloakContainer("quay.io/keycloak/keycloak:25.0").withRealmImportFile("/taskmaster-test-realm.json");
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/" + "taskmaster");
        return keycloak;
    }
}
