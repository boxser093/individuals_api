package net.ilya.individualsapi.service.impl;

import net.ilya.individualsapi.clients.IndividualsClient;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.request.KeyCloakRegistryCredentials;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import net.ilya.individualsapi.dto.response.RegistrationIndividualsResponse;
import net.ilya.individualsapi.service.KeyCloakService;
import net.ilya.individualsapi.util.DateUtilService;
import net.ilya.individualsapi.util.UtilsMethodClass;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.entity.StatusEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static net.ilya.individualsapi.util.ApplicationConstants.SUCCESS_REGISTRATION;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IndividualServiceImplTest {
    @Mock
    private UtilsMethodClass utilsMethodClass;
    @Mock
    private IndividualsClient individualsClient;
    @Mock
    private KeyCloakService keyCloakService;

    @InjectMocks
    private IndividualServiceImpl individualService;

    @Test
    @DisplayName("Authenticate user by login and pass")
    void authenticate_User() {
        //given
        AuthenticationRequest authenticationRequest = AuthenticationRequest.builder()
                .login("test")
                .pass("test1")
                .build();

        AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                .accessToken("test")
                .tokenType("Barer ")
                .refreshToken("test1")
                .expiresIn(300L)
                .build();
        //when
        when(keyCloakService.authUser(any(AuthenticationRequest.class)))
                .thenReturn(Mono.just(authenticationResponse));
        //then
        StepVerifier.create(individualService.authUser(authenticationRequest))
                .expectNextMatches(authenticationResponse1 -> authenticationResponse1.getAccessToken().equals(authenticationResponse.getAccessToken()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Get info by Jwt token")
    void get_Individuals_byId_when_jwt_validate() {
        //given
        JwtAuthenticationToken token = DateUtilService.getToken();
        UUID uuid = UUID.randomUUID();
        IndividualDto individualWithDateAfterSave = DateUtilService.getIndividualWithDateAfterSave().toBuilder()
                .userId(uuid)
                .build();
        //when
        when(utilsMethodClass.parsIdFromToken(any(JwtAuthenticationToken.class)))
                .thenReturn(uuid);
        when(individualsClient.findById(any(UUID.class)))
                .thenReturn(Mono.just(individualWithDateAfterSave));
        //then
        StepVerifier.create(individualService.getIndividuals(token))
                .expectNextMatches(individualDto -> individualDto.getPassportNumber().equals(individualWithDateAfterSave.getPassportNumber()) &&
                        individualDto.getEmail().equals(individualWithDateAfterSave.getEmail()))
                .verifyComplete();
    }

    @Test
    @DisplayName("Create individuals and user in keycloak success result")
    void create_Individuals() {
        //given
        KeyCloakRegistryCredentials gutsBerserkRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        IndividualDto individualWithOutDate = DateUtilService.getIndividualWithOutDate();
        IndividualRegistrationRequest registrationRequest = DateUtilService.getIndividualRegistrationDto().toBuilder()
                .individualDto(individualWithOutDate)
                .keyCloakRegistryCredentials(gutsBerserkRegistryCredentials)
                .build();
        IndividualDto individualWithDateAfterSave = DateUtilService.getIndividualWithDateAfterSave();
        RegistrationIndividualsResponse registrationIndividualsResponse = RegistrationIndividualsResponse.builder()
                .message(SUCCESS_REGISTRATION)
                .build();
        //when
        when(utilsMethodClass.toUserRepresentation(any(IndividualRegistrationRequest.class)))
                .thenReturn(mock(UserRepresentation.class));
        when(individualsClient.create(any(IndividualDto.class)))
                .thenReturn(Mono.just(individualWithDateAfterSave));
        when(keyCloakService.createUser(any(UserRepresentation.class))).thenReturn(Mono.just(registrationIndividualsResponse));
        //then
        StepVerifier.create(individualService.createIndividuals(registrationRequest))
                .expectNextMatches(response -> ((RegistrationIndividualsResponse) response)
                        .getMessage().equals(SUCCESS_REGISTRATION))
                .verifyComplete();
    }

    @Test
    @DisplayName("Update individuals when individuals exist and success result")
    void update_Individuals_when_Individuals_exist() {
        //given
        IndividualDto individualWithOutDateForUpdate = DateUtilService.getIndividualWithOutDateForUpdate();
        //when
        when(individualsClient.update(individualWithOutDateForUpdate)).thenReturn(Mono.just(individualWithOutDateForUpdate.toBuilder()
                .passportNumber("Check1")
                .status(StatusEntity.UPDATED)
                .build()));
        //then
        StepVerifier.create(individualService.updateIndividuals(individualWithOutDateForUpdate))
                .expectNextMatches(individualDto -> individualDto.getPassportNumber().equals("Check1") &&
                        individualDto.getStatus().name().equals("UPDATED"))
                .verifyComplete();
    }

}