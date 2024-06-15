package net.ilya.individualsapi.rest;

import net.ilya.individualsapi.config.KeycloakTestContainers;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.request.KeyCloakRegistryCredentials;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.individualsapi.util.DateUtilService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import org.junit.jupiter.api.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@Import(KeycloakTestContainers.class)
@Profile("test")
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IndividualsApiRestControllerV1Test {
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IndividualsService individualsService;
    private String jwtTest;
    private final UUID idTest = UUID.randomUUID();


    @Test
    @Order(1)
    @DisplayName("Registration new individuals with registration in keycloak new user")
    void registration_Individuals_successful() {
        //given

        IndividualDto individualWithOutDate = DateUtilService.getIndividualWithOutDate();
        KeyCloakRegistryCredentials johnWikRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        IndividualRegistrationRequest individualRegistrationDto = DateUtilService.getIndividualRegistrationDto().toBuilder()
                .keyCloakRegistryCredentials(johnWikRegistryCredentials)
                .individualDto(individualWithOutDate)
                .build();
        IndividualDto individualWithDateAfterSave = DateUtilService.getIndividualWithDateAfterSave().toBuilder()
                .id(idTest)
                .build();
        Mono<IndividualDto> afterSave = Mono.just(individualWithDateAfterSave);

//        when(individualsService.createIndividuals(any(IndividualRegistrationRequest.class)))
//                .thenReturn(afterSave);

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/orchestrator/public/registration")
                .body(Mono.just(individualRegistrationDto), IndividualRegistrationRequest.class)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.message").isNotEmpty();

    }

    @Test
    @Order(2)
    @DisplayName("Auth rigistred user when user exist in keycloak db")
    void auth_user_when_user_exist() {
        //given
//        KeyCloakRegistryCredentials johnWikRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
//        AuthRequestDto authRequestDto = AuthRequestDto.builder()
//                .login(johnWikRegistryCredentials.getUsername())
//                .pass(johnWikRegistryCredentials.getPassword())
//                .build();
//        Keycloak keycloakUser = KeycloakBuilder.builder()
//                .serverUrl(keycloakContainer.getAuthServerUrl())
//                .realm(realmName)
//                .grantType(OAuth2Constants.PASSWORD)
//                .clientId(CLIENT_ID)
//                .clientSecret(CLIENT_SECRET)
//                .username(johnWikRegistryCredentials.getUsername())
//                .password(johnWikRegistryCredentials.getPassword())
//                .build();
//        jwtTest = keycloakUser.tokenManager().getAccessToken().getToken();
//        //when
////        when(keyCloakFactory.getInstance(anyString(), anyString(), anyString(), anyString(), anyString(), anyString()))
////                .thenReturn(keycloakUser);
//        WebTestClient.ResponseSpec response = webTestClient.post()
//                .uri("/api/v1/orchestrator/public/auth")
//                .body(Mono.just(authRequestDto), AuthRequestDto.class)
//                .exchange();

        //then
//        response.expectStatus().isOk()
//                .expectBody()
//                .consumeWith(System.out::println)
//                .jsonPath("$.access_token").isNotEmpty()
//                .jsonPath("$.expires_in").isNotEmpty()
//                .jsonPath("$.refresh_token").isNotEmpty()
//                .jsonPath("$.token_type").isEqualTo("Bearer");
    }

    @Test
    @Order(3)
    @DisplayName("Update individual when individuals exist and token not expired")
    void update_individuals_successful() {
        //given
//        IndividualDto individualWithOutDateForUpdate = DateUtilService.getIndividualWithOutDateForUpdate().toBuilder()
//                .id(idTest)
//                .build();
//        IndividualDto individualWithDateAfterUpdate = DateUtilService.getIndividualWithDateAfterUpdate().toBuilder()
//                .id(idTest)
//                .build();
//        KeyCloakRegistryCredentials johnWikRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
//
//        Keycloak keycloakUser = KeycloakBuilder.builder()
//                .serverUrl(keycloakContainer.getAuthServerUrl())
//                .realm(realmName)
//                .grantType(OAuth2Constants.PASSWORD)
//                .clientId(CLIENT_ID)
//                .clientSecret(CLIENT_SECRET)
//                .username(johnWikRegistryCredentials.getUsername())
//                .password(johnWikRegistryCredentials.getPassword())
//                .build();
//        jwtTest = keycloakUser.tokenManager().getAccessToken().getToken();
//        //when
//        when(individualsService.updateIndividuals(any(IndividualDto.class)))
//                .thenReturn(Mono.just(individualWithDateAfterUpdate));
//        System.out.println("IN update_individuals_successful : " + jwtTest);
//        WebTestClient.ResponseSpec response = webTestClient.post()
//                .uri("/api/v1/orchestrator/update")
//                .headers(headers -> headers.setBearerAuth(jwtTest))
//                .body(Mono.just(individualWithOutDateForUpdate), IndividualDto.class)
//                .exchange();
//        //then
//        response.expectStatus().isOk()
//                .expectBody()
//                .consumeWith(System.out::println)
//                .jsonPath("$.message").isNotEmpty();
    }

    @Test
    @Order(4)
    @DisplayName("Update individual when individuals exist and token not expired")
    void infoForMe() {
//        //given
//        IndividualDto individualWithDateAfterUpdate = DateUtilService.getIndividualWithDateAfterUpdate().toBuilder()
//                .id(idTest)
//                .build();
//        when(individualsService.getIndividuals(idTest)).thenReturn(Mono.just(individualWithDateAfterUpdate));
//        //when
//        WebTestClient.ResponseSpec response = webTestClient.get()
//                .uri("/api/v1/orchestrator/info")
//                .headers(headers -> headers.setBearerAuth(jwtTest))
//                .exchange();
//        //then
//        response.expectStatus().isOk()
//                .expectBody()
//                .consumeWith(System.out::println)
//                .jsonPath("$.id").isEqualTo(idTest)
//                .jsonPath("$.passport_number").isEqualTo(individualWithDateAfterUpdate.getPassportNumber())
//                .jsonPath("$.user_data.first_name").isEqualTo(individualWithDateAfterUpdate.getUserData().getFirstName());

    }
}