package net.ilya.individualsapi.util;

import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.security.Key;
@Service
public class KeycloakClientFactory {
    public Keycloak getUserClientInstance(AuthenticationRequest authenticationRequest, String realm, String clientId, String clientKey, String url){
        return KeycloakBuilder.builder()
                .clientId(clientId)
                .realm(realm)
                .clientSecret(clientKey)
                .serverUrl(url)
                .grantType(OAuth2Constants.PASSWORD)
                .username(authenticationRequest.getLogin())
                .password(authenticationRequest.getPass())
                .build();
    }
}
