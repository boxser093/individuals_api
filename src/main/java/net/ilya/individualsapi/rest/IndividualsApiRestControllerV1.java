package net.ilya.individualsapi.rest;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.individualsapi.service.KeyCloakService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualRegisterDTO;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.AuthRequestDto;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.keycloakdto.ShortAccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static net.ilya.individualsapi.util.ApplicationConstants.PERSON_ID_JWT;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Individuals api orchestrator controller", description = "Operation with individuals")
@RequestMapping("/api/v1/orchestrator")
public class IndividualsApiRestControllerV1 {
    private final KeyCloakService keyCloakService;
    private final IndividualsService individualsService;

    @SecurityRequirement(name = "JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual exist",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Individual not exist by id",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Access Forbidden.",
                    content = @Content(mediaType = "application/json"))})
    @GetMapping("/info")
    public Mono<IndividualDto> infoForMe(@AuthenticationPrincipal JwtAuthenticationToken token) {
        return individualsService.getIndividuals(UUID.fromString(token.getTokenAttributes().get(PERSON_ID_JWT).toString()));
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Access  is allowed",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ShortAccessResponse.class))}),
            @ApiResponse(responseCode = "401", description = "User not auth by credentials",
                    content = @Content)})
    @PostMapping("/public/auth")
    public Mono<?> authUser(@RequestBody Mono<AuthRequestDto> authRequestDtoMono) {
        return authRequestDtoMono.flatMap(keyCloakService::authUser);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual registration successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Error in processing create entity",
                    content = @Content)})
    @PostMapping("/public/registration")
    public Mono<?> registrationIndividuals(@RequestBody Mono<IndividualRegisterDTO> individualRegisterDTOMono) {
        return individualRegisterDTOMono.switchIfEmpty(Mono.error(() -> new BadRequestException("Where Body?")))
                .flatMap(individualRegisterDTO -> individualsService.createIndividuals(individualRegisterDTO)
                        .flatMap(individualDto -> keyCloakService.registerUser(individualDto, individualRegisterDTO.getKeyCloakRegistryCredentials())));
    }

    @SecurityRequirement(name = "JWT")

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Individual update successful",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = IndividualDto.class))}),
            @ApiResponse(responseCode = "400", description = "Error update entity",
                    content = @Content)})
    @PostMapping("/update")
    public Mono<?> updateIndividuals(@RequestBody Mono<IndividualDto> individualDtoMono) {
        return individualDtoMono.flatMap(individualsService::updateIndividuals);
    }

}
