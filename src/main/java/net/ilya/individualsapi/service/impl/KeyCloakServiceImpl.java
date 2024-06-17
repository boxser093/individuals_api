package net.ilya.individualsapi.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import net.ilya.individualsapi.dto.response.RegistrationIndividualsResponse;
import net.ilya.individualsapi.service.KeyCloakService;

import net.ilya.individualsapi.util.KeycloakClientFactory;
import net.ilya.individualsapi.util.UtilsMethodClass;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;

import static net.ilya.individualsapi.util.ApplicationConstants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {
    private final Keycloak keycloak;
    private final KeycloakClientFactory keycloakClientFactory;
    private final UtilsMethodClass utilsMethodClass;
    @Setter
    @Value("${keycloak.realm}")
    private String realmName;
    @Setter
    @Value("${keycloak.client-id}")
    private String clientId;
    @Setter
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Setter
    @Value("${keycloak.auth-url}")
    private String authServerUrl;

    @Override
    public Mono<RegistrationIndividualsResponse> createUser(UserRepresentation userRepresentation) {
        Response response = keycloak.realm(realmName).users().create(userRepresentation);
        return Mono.just(response).filter(response1 -> response1.getStatus() == 201)
                .switchIfEmpty(Mono.error(() -> new BadRequestException(FAILED_REGISTRATION)))
                .flatMap(response1 -> Mono.just(RegistrationIndividualsResponse.builder()
                        .message(SUCCESS_REGISTRATION)
                        .build()));
    }

    @Override
    public Mono<AuthenticationResponse> authUser(AuthenticationRequest authenticationRequest) {
        Keycloak keycloak1 = keycloakClientFactory.getUserClientInstance(authenticationRequest, realmName, clientId, clientSecret, authServerUrl);
        try {
            AccessTokenResponse accessToken = keycloak1.tokenManager().getAccessToken();
            return Mono.just(accessToken)
                    .map(utilsMethodClass::toShortAccessResponse);
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }



}
