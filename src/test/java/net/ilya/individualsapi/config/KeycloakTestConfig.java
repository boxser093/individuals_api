package net.ilya.individualsapi.config;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeAll;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@ContextConfiguration(initializers = KeycloakPropertiesInitializer.class)
public abstract class KeycloakTestConfig {

    static final String CLIENT_ID = "orchestrator";
    static final String CLIENT_SECRET = "nbMPojc3q2Uxmnk5IhxqEZscUVHB5ZPM";
    static final KeycloakContainer CONTAINER;

    static {
        CONTAINER = new KeycloakContainer("quay.io/keycloak/keycloak:24.0.4")
                .withRamPercentage(50, 70)
                .withRealmImportFile("/realm-export.json");
        CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri",
                () -> "http://localhost:" + CONTAINER.getFirstMappedPort() + "/realms/orchestrator");
        registry.add("keycloak.admin.server-url", () -> "http://localhost:" + CONTAINER.getFirstMappedPort() + "/");
        registry.add("keycloak.admin.realm", () -> "orchestrator");
        registry.add("keycloak.admin.resource", () -> CLIENT_ID);
        registry.add("keycloak.admin.credentials.secret", () -> CLIENT_SECRET);
    }

    @Bean
    @Primary
    public Keycloak keycloak() {
        return CONTAINER.getKeycloakAdminClient();
    }
}
