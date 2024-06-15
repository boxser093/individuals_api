package net.ilya.individualsapi.rest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.ilya.individualsapi.dto.request.AuthenticationRequest;
import net.ilya.individualsapi.dto.request.IndividualRegistrationRequest;
import net.ilya.individualsapi.service.IndividualsService;
import net.ilya.users._api_microservice_on_webflux.dtoforuserservice.dto.IndividualDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/orchestrator")
public class IndividualsApiRestControllerV1 {
    private IndividualsService individualsService;

    @GetMapping("/info")
    public Mono<IndividualDto> infoForMe(@AuthenticationPrincipal JwtAuthenticationToken token) {
        return individualsService.getIndividuals(token);
    }
    @PostMapping("/public/auth")
    public Mono<?> authUser(@RequestBody Mono<AuthenticationRequest> authRequestDtoMono) {
        return authRequestDtoMono.flatMap(individualsService::authUser);
    }
    @PostMapping("/public/registration")
    public Mono<?> registrationIndividuals(@RequestBody Mono<IndividualRegistrationRequest> individualRegisterDTOMono) {
        return individualRegisterDTOMono.flatMap(individualsService::createIndividuals);
    }

    @PostMapping("/update")
    public Mono<?> updateIndividuals(@RequestBody Mono<IndividualDto> individualDtoMono) {
        return individualDtoMono.flatMap(individualsService::updateIndividuals);
    }

}
