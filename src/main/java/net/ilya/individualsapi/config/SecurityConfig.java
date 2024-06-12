package net.ilya.individualsapi.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@ConditionalOnProperty(name = "keycloak.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.
                csrf().disable()
                .cors().disable()
                .authorizeExchange(configurer -> configurer.pathMatchers("/api/v1/orchestrator/public/*").permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(customizer -> customizer.jwt(Customizer.withDefaults()))
                .build();
    }
}
