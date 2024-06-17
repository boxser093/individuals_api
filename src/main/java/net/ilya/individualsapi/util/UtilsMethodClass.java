package net.ilya.individualsapi.util;

import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.dto.response.AuthenticationResponse;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static net.ilya.individualsapi.util.ApplicationConstants.PERSON_ID_JWT;

@Service
public class UtilsMethodClass {
    public UUID parsIdFromToken(JwtAuthenticationToken token){
        return UUID.fromString(token.getTokenAttributes().get(PERSON_ID_JWT).toString());
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
    public AuthenticationResponse toShortAccessResponse(AccessTokenResponse accessTokenResponse) {
        return AuthenticationResponse.builder()
                .accessToken(accessTokenResponse.getToken())
                .expiresIn(accessTokenResponse.getExpiresIn())
                .refreshToken(accessTokenResponse.getRefreshToken())
                .tokenType(accessTokenResponse.getTokenType())
                .build();
    }
}
