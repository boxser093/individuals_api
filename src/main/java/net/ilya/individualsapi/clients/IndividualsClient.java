package net.ilya.individualsapi.clients;

import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;

import java.util.UUID;

public interface IndividualsClient extends GenericService<IndividualDto, UUID> {
}
