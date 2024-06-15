package net.ilya.individualsapi.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class IndividualRegistrationRequest {
    private IndividualDto individualDto;
    private KeyCloakRegistryCredentials keyCloakRegistryCredentials;
}
