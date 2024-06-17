package net.ilya.individualsapi.service.impl;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.KeyCloakRegistryCredentials;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import net.ilya.individualsapi.dto.response.RegistrationIndividualsResponse;
import net.ilya.individualsapi.util.DateUtilService;
import net.ilya.individualsapi.util.KeycloakClientFactory;
import net.ilya.individualsapi.util.UtilsMethodClass;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.token.TokenManager;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import java.net.URI;
import java.net.URISyntaxException;

import static net.ilya.individualsapi.util.ApplicationConstants.FAILED_REGISTRATION;
import static net.ilya.individualsapi.util.ApplicationConstants.SUCCESS_REGISTRATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KeyCloakServiceImplTest {
    @Mock
    private UtilsMethodClass utilsMethodClass;
    @Mock
    private KeycloakClientFactory keycloakClientFactory;
    @Mock
    private Keycloak keycloak;
    @Mock
    private RealmResource realmResource;
    @Mock
    private UsersResource usersResource;
    @Mock
    private TokenManager tokenManager;
    @InjectMocks
    private KeyCloakServiceImpl keyCloakService;

    @BeforeEach
    void setUpd() {
        keyCloakService.setRealmName("realmName");
        keyCloakService.setClientId("ClientId");
        keyCloakService.setClientSecret("secret");
        keyCloakService.setAuthServerUrl(" http://localhost:9999/");

    }

    @Test
    void authUserShouldAuthenticateSuccessfully() {
        //given
        KeyCloakRegistryCredentials gutsBerserkRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        AuthenticationRequest authRequestDto = AuthenticationRequest.builder()
                .login(gutsBerserkRegistryCredentials.getUsername())
                .pass(gutsBerserkRegistryCredentials.getPassword())
                .build();
        AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
        accessTokenResponse.setToken("access-token");
        accessTokenResponse.setExpiresIn(300);
        accessTokenResponse.setRefreshToken("refresh-test");
        accessTokenResponse.setTokenType("Barer");
        AuthenticationResponse authenticationResponse1 = AuthenticationResponse.builder()
                .accessToken("check")
                .refreshToken("check1")
                .expiresIn(300L)
                .tokenType("Barer ")
                .build();

        //when
        when(keycloakClientFactory.getUserClientInstance(any(AuthenticationRequest.class),anyString(),anyString(),anyString(),anyString())).thenReturn(keycloak);
        when(keycloak.tokenManager()).thenReturn(tokenManager);
        when(tokenManager.getAccessToken()).thenReturn(accessTokenResponse);
        when(utilsMethodClass.toShortAccessResponse(any(AccessTokenResponse.class))).thenReturn(authenticationResponse1);

        //then
        StepVerifier.create(keyCloakService.authUser(authRequestDto))
                .expectNextMatches(authenticationResponse -> authenticationResponse.getAccessToken().equals("check") &&
                        authenticationResponse.getRefreshToken().equals("check1"))
                .verifyComplete();
    }

    @Test
    void createUserShouldReturnSuccess() throws URISyntaxException {
        //given
        UserRepresentation userRepresentationJohnWik = DateUtilService.getUserRepresentationJohnWik();
        Response response = Response.created(new URI("http:/localhost:test")).build();
        //when
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);
        //then
        StepVerifier.create(keyCloakService.createUser(userRepresentationJohnWik))
                .expectNextMatches(response1 -> ((RegistrationIndividualsResponse) response1).getMessage()
                        .equals(SUCCESS_REGISTRATION))
                .verifyComplete();
    }

    @Test
    void createUserShouldReturnErrorOnFailure() throws URISyntaxException {

        UserRepresentation userRepresentation = new UserRepresentation();
        Response response = Response.status(Response.Status.BAD_REQUEST).build();
        when(keycloak.realm(anyString())).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.create(any(UserRepresentation.class))).thenReturn(response);

        StepVerifier.create(keyCloakService.createUser(userRepresentation))
                .expectErrorMatches(throwable -> throwable instanceof BadRequestException &&
                        throwable.getMessage().contains(FAILED_REGISTRATION))
                .verify();

        verify(usersResource).create(userRepresentation);
    }


}