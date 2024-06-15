package net.ilya.individualsapi.config;


import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class KeycloakTestContainers {
    public static final String CLIENT_ID = "individuals-cli";
    public static String realmName = "orchestrator";
    public static String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:24.0.4";
    public static String realmImportFile = "/realm-export.json";
    public static final String CLIENT_SECRET = "nbMPojc3q2Uxmnk5IhxqEZscUVHB5ZPM";

    public static KeycloakContainer keycloakContainer = new KeycloakContainer(KEYCLOAK_IMAGE)
            .withRealmImportFile(realmImportFile)
            .withRamPercentage(50, 70);

    static {
        keycloakContainer.start();
    }


    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloakContainer.getAuthServerUrl() + "/realms/"+realmName);
        registry.add("keycloak.auth-url", () -> keycloakContainer.getAuthServerUrl());
    }

    @Bean
    @Primary
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakContainer.getAuthServerUrl())
                .realm(realmName)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(CLIENT_ID)
                .clientSecret(CLIENT_SECRET)
                .build();
    }
}
