package net.ilya.individualsapi.rest;

import net.ilya.individualsapi.clients.IndividualsClient;
import net.ilya.individualsapi.config.KeycloakTestContainers;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.request.KeyCloakRegistryCredentials;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.individualsapi.service.KeyCloakService;
import net.ilya.individualsapi.util.DateUtilService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.entity.StatusEntity;
import org.junit.jupiter.api.*;

import org.keycloak.admin.client.Keycloak;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(locations = "classpath:application-test.yaml")
class IndividualsApiRestControllerV1Test extends KeycloakTestContainers {

    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IndividualsClient individualsClient;
    private final UUID idTest = UUID.randomUUID();
    private static String jwtTest;

    @Test
    @Order(1)
    @DisplayName("Registration new individuals with registration in keycloak new user")
    void registration_Individuals_successful() {
        //given
        IndividualDto individualWithOutDate = DateUtilService.getIndividualWithOutDate();
        KeyCloakRegistryCredentials gutsBerserkRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        IndividualRegistrationRequest individualRegistrationDto = DateUtilService.getIndividualRegistrationDto().toBuilder()
                .keyCloakRegistryCredentials(gutsBerserkRegistryCredentials)
                .individualDto(individualWithOutDate)
                .build();
        IndividualDto individualWithDateAfterSave = DateUtilService.getIndividualWithDateAfterSave().toBuilder()
                .id(idTest)
                .build();
        Mono<IndividualDto> afterSave = Mono.just(individualWithDateAfterSave);

        when(individualsClient.create(any(IndividualDto.class)))
                .thenReturn(afterSave);

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
        KeyCloakRegistryCredentials gutsBerserkRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        AuthenticationRequest authRequestDto = AuthenticationRequest.builder()
                .login(gutsBerserkRegistryCredentials.getUsername())
                .pass(gutsBerserkRegistryCredentials.getPassword())
                .build();

        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/orchestrator/public/auth")
                .body(Mono.just(authRequestDto), AuthenticationRequest.class)
                .exchange();

        // then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.access_token").value(token -> jwtTest = String.valueOf(token))
                .jsonPath("$.expires_in").isNotEmpty()
                .jsonPath("$.refresh_token").isNotEmpty()
                .jsonPath("$.token_type").isEqualTo("Bearer");
    }

    @Test
    @Order(3)
    @DisplayName("Update individual when individuals exist and token not expired")
    void update_individuals_successful() {
        //given

        IndividualDto individualWithOutDateForUpdate = DateUtilService.getIndividualWithOutDateForUpdate().toBuilder()
                .id(idTest)
                .build();
        IndividualDto individualWithDateAfterUpdate = DateUtilService.getIndividualWithDateAfterUpdate().toBuilder()
                .id(idTest)
                .build();
        //when
        when(individualsClient.update(any(IndividualDto.class)))
                .thenReturn(Mono.just(individualWithDateAfterUpdate));

        WebTestClient.ResponseSpec response = webTestClient.put()
                .uri("/api/v1/orchestrator/update")
                .headers(headers -> headers.setBearerAuth(jwtTest))
                .body(Mono.just(individualWithOutDateForUpdate), IndividualDto.class)
                .exchange();

        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.passport_number").isEqualTo(individualWithDateAfterUpdate.getPassportNumber())
                .jsonPath("$.phone_number").isEqualTo(individualWithDateAfterUpdate.getPhoneNumber())
                .jsonPath("$.email").isEqualTo(individualWithDateAfterUpdate.getEmail())
                .jsonPath("$.status").isEqualTo("UPDATED");
    }

    @Test
    @Order(4)
    @DisplayName("Update individual when individuals exist and token not expired")
    void infoForMe() {
        //given
        IndividualDto individualWithDateAfterUpdate = DateUtilService.getIndividualWithDateAfterUpdate().toBuilder()
                .id(idTest)
                .build();
        when(individualsClient.findById(any(UUID.class)))
                .thenReturn(Mono.just(individualWithDateAfterUpdate));
        //when
        WebTestClient.ResponseSpec response = webTestClient.get()
                .uri("/api/v1/orchestrator/info")
                .headers(headers -> headers.setBearerAuth(jwtTest))
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.passport_number").isEqualTo(individualWithDateAfterUpdate.getPassportNumber())
                .jsonPath("$.phone_number").isEqualTo(individualWithDateAfterUpdate.getPhoneNumber())
                .jsonPath("$.email").isEqualTo(individualWithDateAfterUpdate.getEmail())
                .jsonPath("$.status").isEqualTo("UPDATED");

    }
}