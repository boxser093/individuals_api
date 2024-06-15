package net.ilya.individualsapi.clients.impl;

import jakarta.ws.rs.BadRequestException;
import lombok.AllArgsConstructor;
import net.ilya.individualsapi.clients.IndividualsClient;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.ErrorResponse;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;


@Service
@AllArgsConstructor
public class IndividualsClientImpl implements IndividualsClient {
    private final WebClient webClient;
    @Override
    public Mono<IndividualDto> findById(UUID uuid) {
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
    public Mono<IndividualDto> create(IndividualDto individualDto) {
        return webClient.post()
                .uri("http://localhost:8082/api/v1/individuals/")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(individualDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody.getMessage())))
                )
                .bodyToMono(IndividualDto.class);
    }

    @Override
    public Mono<IndividualDto> update(IndividualDto individualDto) {
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

    @Override
    public Mono<IndividualDto> deleted(UUID uuid) {
        return webClient.delete()
                .uri("http://localhost:8082/api/v1/individuals/{id}",uuid)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody.getMessage())))
                )
                .bodyToMono(IndividualDto.class);
    }

    @Override
    public Flux<IndividualDto> findAll() {
        return webClient.get().uri("http://localhost:8082/api/v1/individuals/list")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(ErrorResponse.class)
                                .flatMap(errorBody -> Mono.error(new BadRequestException(errorBody.getMessage())))
                )
                .bodyToFlux(IndividualDto.class);
    }
}
