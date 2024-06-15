package net.ilya.individualsapi.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class KeyCloakServiceImplTest {
    @Mock
    private Keycloak keycloak;
    @InjectMocks
    private KeyCloakServiceImpl keyCloakService;

    @Test
    void authUser() {

    }

    @Test
    void registerUser() {
    }

}