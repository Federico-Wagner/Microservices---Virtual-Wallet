package com.virtualWallet.AuthenticatorMs.service;

import static org.junit.jupiter.api.Assertions.*;
import com.virtualWallet.AuthenticatorMs.enumerators.RolesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;


class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        // inyectamos los valores de @Value manualmente
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY", "my-super-secret-key-which-is-long-enough");
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY_DURATION_MINUTES", 60L); // 60 min
    }

    @Test
    void generateToken_shouldReturnNonNullToken() {
        String token = jwtService.generateToken("user123", RolesEnum.CLIENT);
        assertNotNull(token);
    }

    @Test
    void validateTokenAndGetUserIdentificator_shouldReturnUserId_whenTokenIsValid() {
        String token = jwtService.generateToken("user123", RolesEnum.CLIENT);
        String userId = jwtService.validateTokenAndGetUserIdentificator(token);
        assertEquals("user123", userId);
    }

    @Test
    void validateTokenAndGetUserIdentificator_shouldReturnNull_whenTokenIsInvalid() {
        String invalidToken = "invalid.token.value";
        String userId = jwtService.validateTokenAndGetUserIdentificator(invalidToken);
        assertNull(userId);
    }

    @Test
    void getTokenRole_shouldReturnCorrectRole() {
        String token = jwtService.generateToken("user123", RolesEnum.CLIENT);
        RolesEnum role = jwtService.getTokenRole(token);
        assertEquals(RolesEnum.CLIENT, role);
    }

    @Test
    void validateToken_shouldReturnNull_whenTokenExpired() throws InterruptedException {
        // token with zero duration
        ReflectionTestUtils.setField(jwtService, "SECRET_KEY_DURATION_MINUTES", 0L);
        String token = jwtService.generateToken("user123", RolesEnum.CLIENT);

        // Wait to ensure token expiration
        Thread.sleep(1000);

        String userId = jwtService.validateTokenAndGetUserIdentificator(token);
        assertNull(userId);
    }
}
