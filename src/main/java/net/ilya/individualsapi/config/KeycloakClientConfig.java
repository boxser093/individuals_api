package net.ilya.individualsapi.config;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;


@Slf4j
@Configuration
public class KeycloakClientConfig {

    @Value("${keycloak.admin.server-url}")
    private String authServerUrl;

    @Value("${keycloak.admin.realm}")
    private String realmName;

    @Value("${keycloak.admin.resource}")
    private String clientId;

    @Value("${keycloak.admin.credentials.secret}")
    private String secret;
    @Lazy
    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realmName)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .clientId(clientId)
                .clientSecret(secret)
                .build();
    }
}
