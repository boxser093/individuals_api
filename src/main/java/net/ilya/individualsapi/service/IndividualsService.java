package net.ilya.individualsapi.service;

import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualsService {
    Mono<IndividualDto> getIndividuals(JwtAuthenticationToken token);
    Mono<?> createIndividuals(IndividualRegistrationRequest individualDto);
    Mono<IndividualDto> updateIndividuals(IndividualDto individualDto);

    Mono<?> authUser(AuthenticationRequest authenticationRequest);

}
