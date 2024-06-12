package net.ilya.individualsapi.config;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

public class KeycloakPropertiesInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        TestPropertyValues.of(
                "keycloak.admin.server-url=http://localhost:" + KeycloakTestConfig.CONTAINER.getFirstMappedPort(),
                "keycloak.admin.realm=orchestrator",
                "keycloak.admin.resource=" + KeycloakTestConfig.CLIENT_ID,
                "keycloak.admin.credentials.secret=" + KeycloakTestConfig.CLIENT_SECRET
        ).applyTo(applicationContext.getEnvironment());
    }
}
