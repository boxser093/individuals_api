package net.ilya.individualsapi.service;

import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualRegisterDTO;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface IndividualsService {
    Mono<IndividualDto> getIndividuals(UUID uuid);
    Mono<IndividualDto> createIndividuals(IndividualRegisterDTO individualDto);
    Mono<IndividualDto> updateIndividuals(IndividualDto individualDto);

}
