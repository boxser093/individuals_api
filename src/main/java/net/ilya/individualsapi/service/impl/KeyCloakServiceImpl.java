package net.ilya.individualsapi.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.service.KeyCloakService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.OrchestratorResponse;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.AuthRequestDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.KeyCloakRegistryCredentials;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.KeyCloakUserRepresentation;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.ShortAccessResponse;
import org.apache.http.HttpStatus;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


import java.util.List;

import static net.ilya.individualsapi.util.ApplicationConstants.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class KeyCloakServiceImpl implements KeyCloakService {
    @Lazy
    private final Keycloak keycloak;

    @Value("${keycloak.admin.realm}")
    public String realm;
    @Value("${keycloak.admin.resource}")
    public String clientId;
    @Value("${keycloak.admin.credentials.secret}")
    public String clientSecret;
    @Value("${keycloak.admin.server-url}")
    public String serverUrl;

    @Override
    public Mono<?> registerUser(IndividualDto individualDto, KeyCloakRegistryCredentials keyCloakRegistryCredentials) {
        UserRepresentation newUser = toUserRepresentation(individualDto, keyCloakRegistryCredentials);
            Response response = keycloak.realm(realm).users().create(newUser);

        return Mono.just(response).filter(response1 -> response1.getStatus() == 201)
                .switchIfEmpty(Mono.error(() -> new BadRequestException(FAILED_REGISTRATION)))
                .flatMap(response1 -> Mono.just(keycloak.realm(realm).users().searchByUsername(newUser.getUsername(), true).getFirst()));

    }

    @Override
    public Mono<ShortAccessResponse> authUser(AuthRequestDto authRequestDto) {
        Keycloak authUser = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .grantType(OAuth2Constants.PASSWORD)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .username(authRequestDto.getLogin())
                .password(authRequestDto.getPass())
                .build();
        try {
            AccessTokenResponse accessToken = authUser.tokenManager().getAccessToken();
            return Mono.just(accessToken)
                    .map(this::toShortAccessResponse);
        } catch (Exception e) {
            throw new AccessDeniedException(e.getMessage());
        }
    }

    @Override
    public Mono<?> updateUser(IndividualDto individualDto) {
        UserRepresentation update = this.toUpdateUserRepresentationBeforeRegistration(individualDto);
        keycloak.realm(realm).users().get(update.getId()).update(update);
        UserRepresentation representation = keycloak.realm(realm).users().get(update.getId()).toRepresentation();
        return Mono.just(representation).filter(userRepresentation -> userRepresentation.getFirstName().equals(individualDto.getUserData().getFirstName()) & userRepresentation.getLastName().equals(individualDto.getUserData().getLastName()))
                .flatMap(userRepresentation -> Mono.just(OrchestratorResponse.builder()
                                .statusCod(HttpStatus.SC_CREATED)
                                .message(SUCCESS_UPDATE)
                                .build())
                        .switchIfEmpty(Mono.error(() -> new BadRequestException(FAILED_REGISTRATION))));
    }

    @Override
    public UserRepresentation toUserRepresentation(IndividualDto individualDto, KeyCloakRegistryCredentials keyCloakRegistrCredentials) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(keyCloakRegistrCredentials.getUsername());
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setFirstName(individualDto.getUserData().getFirstName());
        newUser.setLastName(individualDto.getUserData().getLastName());
        newUser.setEmail(individualDto.getEmail());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(keyCloakRegistrCredentials.getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        newUser.setCredentials(List.of(credentialRepresentation));
        newUser.singleAttribute("personId", individualDto.getId().toString());
        return newUser;
    }

    private ShortAccessResponse toShortAccessResponse(AccessTokenResponse accessTokenResponse) {
        return ShortAccessResponse.builder()
                .accessToken(accessTokenResponse.getToken())
                .expiresIn(accessTokenResponse.getExpiresIn())
                .refreshToken(accessTokenResponse.getRefreshToken())
                .tokenType(accessTokenResponse.getTokenType())
                .build();
    }

    private UserRepresentation toUpdateUserRepresentationBeforeRegistration(IndividualDto individualDto) {
        UserRepresentation first = keycloak.realm(realm).users().searchByAttributes(individualDto.getId().toString()).getFirst();
        first.setFirstName(individualDto.getUserData().getFirstName());
        first.setLastName(individualDto.getUserData().getLastName());
        first.setEmail(individualDto.getEmail());
        first.setEnabled(true);
        first.setEmailVerified(true);
        return first;
    }

}
