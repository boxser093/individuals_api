package net.ilya.individualsapi.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.clients.IndividualsClient;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.individualsapi.service.KeyCloakService;
import net.ilya.individualsapi.util.UtilsMethodClass;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static net.ilya.individualsapi.util.ApplicationConstants.PERSON_ID_JWT;

@Slf4j
@Service
@AllArgsConstructor
public class IndividualServiceImpl implements IndividualsService {
    private IndividualsClient individualsClient;
    private KeyCloakService keyCloakService;
    private UtilsMethodClass utilsMethodClass;
    @Override
    public Mono<IndividualDto> getIndividuals(JwtAuthenticationToken token) {
        UUID uuid = utilsMethodClass.parsIdFromToken(token);
        log.info("IN IndividualServiceImpl - getIndividuals {},", uuid);
        return individualsClient.findById(uuid);
    }

    @Override
    public Mono<?> createIndividuals(IndividualRegistrationRequest individualRegistrationDto) {
        log.info("IN IndividualServiceImpl - createIndividuals {},", individualRegistrationDto);
        return individualsClient.create(individualRegistrationDto.getIndividualDto())
                .flatMap(individualDto -> keyCloakService.createUser(utilsMethodClass.toUserRepresentation(individualRegistrationDto.toBuilder()
                        .individualDto(individualDto)
                        .build())));
    }

    @Override
    public Mono<IndividualDto> updateIndividuals(IndividualDto individualDto) {
        log.info("IN IndividualServiceImpl - updateIndividuals {},", individualDto);
        return individualsClient.update(individualDto);
    }

    @Override
    public Mono<AuthenticationResponse> authUser(AuthenticationRequest authenticationRequest) {
        log.info("IN IndividualServiceImpl - authUser {},", authenticationRequest);
        return keyCloakService.authUser(authenticationRequest);
    }


}
