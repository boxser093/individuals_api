package net.ilya.individualsapi.rest;

import dasniko.testcontainers.keycloak.KeycloakContainer;
import jakarta.ws.rs.core.Response;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.individualsapi.service.KeyCloakService;
import net.ilya.individualsapi.util.DateUtilService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualRegisterDTO;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.AuthRequestDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.KeyCloakRegistryCredentials;
import org.junit.jupiter.api.*;

import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import org.keycloak.admin.client.Keycloak;

import java.security.Key;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@AutoConfigureWebTestClient
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.main.allow-bean-definition-overriding=true")
public class IndividualsApiRestControllerV1Test {


    @Autowired
    private KeyCloakService keyCloakService;
    @Autowired
    private WebTestClient webTestClient;
    @MockBean
    private IndividualsService individualsService;


    @Test
    @Order(1)
    void registrationIndividuals() {
        //given

        IndividualDto individualWithOutDate = DateUtilService.getIndividualWithOutDate();
        KeyCloakRegistryCredentials johnWikRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        IndividualRegisterDTO individualRegistrationDto = DateUtilService.getIndividualRegistrationDto().toBuilder()
                .keyCloakRegistryCredentials(johnWikRegistryCredentials)
                .individualDto(individualWithOutDate)
                .build();
        IndividualDto individualWithDateAfterSave = DateUtilService.getIndividualWithDateAfterSave();

        when(individualsService.createIndividuals(any(IndividualRegisterDTO.class)))
                .thenReturn(Mono.just(individualWithDateAfterSave));
        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/orchestrator/public/registration")
                .body(Mono.just(individualRegistrationDto), IndividualRegisterDTO.class)
                .exchange();
        //then
        response.
                expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println);
//                .jsonPath("$.id").isNotEmpty()
//                .jsonPath("$.passport_number").isEqualTo(individual.getPassportNumber())
//                .jsonPath("$.phone_number").isEqualTo(individual.getPhoneNumber());

    }

    @Test
    @Order(3)
    void authUser() {
        //given
        KeyCloakRegistryCredentials johnWikRegistryCredentials = DateUtilService.getGutsBerserkRegistryCredentials();
        AuthRequestDto build = AuthRequestDto.builder()
                .login("g.berserk")
                .pass("password")
                .build();

        //when
        WebTestClient.ResponseSpec response = webTestClient.post()
                .uri("/api/v1/orchestrator/public/auth")
                .body(Mono.just(build), AuthRequestDto.class)
                .exchange();
        //then
        response.expectStatus().isOk()
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.access_token").isNotEmpty()
                .jsonPath("$.expires_in").isNotEmpty()
                .jsonPath("$.refresh_token").isNotEmpty()
                .jsonPath("$.token_type").isEqualTo("Bearer");
    }

    @Test
    @Order(4)
    void updateIndividuals() {
    }

    @Test
    @Order(5)
    void infoForMe() {

    }
}