package net.ilya.individualsapi.service;

import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import org.keycloak.representations.idm.UserRepresentation;
import reactor.core.publisher.Mono;

public interface KeyCloakService {
    Mono<?> authUser(AuthenticationRequest authenticationRequest);

    Mono<?> createUser(UserRepresentation userRepresentation);

}
