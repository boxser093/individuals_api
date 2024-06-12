package net.ilya.individualsapi.clients;

import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.ErrorResponse;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualRegisterDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IndividualsClient implements IndividualsService {
    private final WebClient webClient;

    @Override
    public Mono<IndividualDto> getIndividuals(UUID uuid) {
        return webClient.get().uri("http://localhost:8082/api/v1/individuals/{uuid}", uuid)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody.getMessage())))
                )
                .bodyToMono(IndividualDto.class);
    }

    @Override
    public Mono<IndividualDto> createIndividuals(IndividualRegisterDTO individualDto) {
        return webClient.post()
                .uri("http://localhost:8082/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(individualDto.getIndividualDto())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody.getMessage())))
                )
                .bodyToMono(IndividualDto.class);
    }

    @Override
    public Mono<IndividualDto> updateIndividuals(IndividualDto individualDto) {
        return webClient.put()
                .uri("http://localhost:8082/api/v1/individuals/")
                .bodyValue(individualDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody.getMessage())))
                )
                .bodyToMono(IndividualDto.class);
    }

}
