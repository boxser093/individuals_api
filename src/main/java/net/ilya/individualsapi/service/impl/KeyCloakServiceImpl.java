package net.ilya.individualsapi.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import net.ilya.individualsapi.dto.response.RegistrationIndividualsResponse;
import net.ilya.individualsapi.service.KeyCloakService;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
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

    @Value("${keycloak.realm}")
    private String realmName;
    @Value("${keycloak.client-id}")
    private String clientId;
    @Value("${keycloak.client-secret}")
    private String clientSecret;
    @Value("${keycloak.auth-url}")
    private String authServerUrl;

    //TODO:1
    // 1. Разобраться с переменными класса,
    // 2. Проверить аутонтификацию и регистрацию.,,
    @Override
    public Mono<?> createUser(UserRepresentation userRepresentation) {
        Response response = keycloak.realm(realmName).users().create(userRepresentation);
        return Mono.just(response).filter(response1 -> response1.getStatus() == 201)
                .switchIfEmpty(Mono.error(() -> new BadRequestException(FAILED_REGISTRATION)))
                .flatMap(response1 -> Mono.just(RegistrationIndividualsResponse.builder()
                        .message(SUCCESS_REGISTRATION)
                        .build()));
    }

    @Override
    public Mono<AuthenticationResponse> authUser(AuthenticationRequest authenticationRequest) {
        try {
            AccessTokenResponse accessToken = this.getUserClientInstance(authenticationRequest)
                    .tokenManager().getAccessToken();
            return Mono.just(accessToken)
                    .map(this::toShortAccessResponse);
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    public Keycloak getUserClientInstance(AuthenticationRequest authenticationRequest) {
        return KeycloakBuilder.builder()
                .serverUrl(authServerUrl)
                .realm(realmName)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(authenticationRequest.getLogin())
                .password(authenticationRequest.getPass())
                .build();
    }

    public UserRepresentation toUserRepresentation(IndividualRegistrationRequest individualDto) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(individualDto.getKeyCloakRegistryCredentials().getUsername());
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setFirstName(individualDto.getIndividualDto().getUserData().getFirstName());
        newUser.setLastName(individualDto.getIndividualDto().getUserData().getLastName());
        newUser.setEmail(individualDto.getIndividualDto().getEmail());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(individualDto.getKeyCloakRegistryCredentials().getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        newUser.setCredentials(List.of(credentialRepresentation));
        newUser.singleAttribute("personId", individualDto.getIndividualDto().getId().toString());
        return newUser;
    }

    private AuthenticationResponse toShortAccessResponse(AccessTokenResponse accessTokenResponse) {
        return AuthenticationResponse.builder()
                .accessToken(accessTokenResponse.getToken())
                .expiresIn(accessTokenResponse.getExpiresIn())
                .refreshToken(accessTokenResponse.getRefreshToken())
                .tokenType(accessTokenResponse.getTokenType())
                .build();
    }

}
