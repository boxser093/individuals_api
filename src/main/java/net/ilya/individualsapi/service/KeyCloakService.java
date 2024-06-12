package net.ilya.individualsapi.service;

import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.AuthRequestDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.KeyCloakRegistryCredentials;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.KeyCloakUserRepresentation;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.ShortAccessResponse;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public interface KeyCloakService {
    Mono<ShortAccessResponse> authUser(AuthRequestDto authRequestDto);

    Mono<?> registerUser(IndividualDto individualDto, KeyCloakRegistryCredentials keyCloakRegistryCredentials);

    Mono<?> updateUser(IndividualDto individualDto);
    UserRepresentation toUserRepresentation(IndividualDto individualDto, KeyCloakRegistryCredentials keyCloakRegistrCredentials);

}
