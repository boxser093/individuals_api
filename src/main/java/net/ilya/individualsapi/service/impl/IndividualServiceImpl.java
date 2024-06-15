package net.ilya.individualsapi.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.clients.IndividualsClient;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.individualsapi.service.KeyCloakService;
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

    //TODO:
    // 1. Дописать мапперы,
    // 2. Провертить аргументы и параметры методов,,
    @Override
    public Mono<IndividualDto> getIndividuals(JwtAuthenticationToken token) {
        UUID uuid = UUID.fromString(token.getTokenAttributes().get(PERSON_ID_JWT).toString());
        log.info("IN IndividualServiceImpl - getIndividuals {},", uuid);
        return individualsClient.findById(UUID.fromString(token.getTokenAttributes().get(PERSON_ID_JWT).toString()));
    }

    @Override
    public Mono<?> createIndividuals(IndividualRegistrationRequest individualRegistrationDto) {
        log.info("IN IndividualServiceImpl - createIndividuals {},", individualRegistrationDto);
        return individualsClient.create(individualRegistrationDto.getIndividualDto())
                .flatMap(individualDto -> keyCloakService.createUser(this.toUserRepresentation(individualRegistrationDto.toBuilder()
                        .individualDto(individualDto)
                        .build())));
    }

    @Override
    public Mono<IndividualDto> updateIndividuals(IndividualDto individualDto) {
        log.info("IN IndividualServiceImpl - updateIndividuals {},", individualDto);
        return individualsClient.update(individualDto);
    }

    @Override
    public Mono<?> authUser(AuthenticationRequest authenticationRequest) {
        log.info("IN IndividualServiceImpl - authUser {},", authenticationRequest);
        return keyCloakService.authUser(authenticationRequest);
    }

    @Override
    public Mono<?> mapToResponse(IndividualDto individualDto) {
        return null;
    }

    public UserRepresentation toUserRepresentation(IndividualRegistrationRequest individualDto) {
        UserRepresentation newUser = new UserRepresentation();
        newUser.setUsername(individualDto.getKeyCloakRegistryCredentials().getUsername());
        newUser.setEnabled(true);
        newUser.setEmailVerified(true);
        newUser.setFirstName(individualDto.getIndividualDto().getUserData().getFirstName());
        newUser.setLastName(individualDto.getIndividualDto().getUserData().getLastName());
        newUser.setEmail(individualDto.getIndividualDto().getEmail());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(individualDto.getKeyCloakRegistryCredentials().getPassword());
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setTemporary(false);
        newUser.setCredentials(List.of(credentialRepresentation));
        newUser.singleAttribute("personId", individualDto.getIndividualDto().getId().toString());
        return newUser;
    }

}
