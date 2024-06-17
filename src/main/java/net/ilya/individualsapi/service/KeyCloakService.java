package net.ilya.individualsapi.service;

import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import net.ilya.individualsapi.dto.response.RegistrationIndividualsResponse;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public interface KeyCloakService {
    Mono<AuthenticationResponse> authUser(AuthenticationRequest authenticationRequest);

    Mono<RegistrationIndividualsResponse> createUser(UserRepresentation userRepresentation);

}
