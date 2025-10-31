package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;


class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // 🔧 Inyectamos los valores de @Value manualmente usando reflexión
        setPrivateField(jwtService, "SECRET_KEY", "mySuperSecretKeyForJwtMySuperSecretKeyForJwt"); // >= 32 bytes
        setPrivateField(jwtService, "TOKEN_DURATION_MINUTES", 1L);
    }

    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void generateToken_shouldReturnValidJwtString() {
        TokenRequestDTO tokenRequestDTO = new TokenRequestDTO("10", "20-98765432-1", "CLIENT");
        String token = jwtService.generateToken(tokenRequestDTO);
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3, "El token debería tener 3 secciones JWT (header.payload.signature)");
    }

    @Test
    void authenticateTokenAndGetData_shouldReturnValidTokenDTO_whenTokenIsValid() {
        // Arrange
        TokenRequestDTO tokenRequestDTO = new TokenRequestDTO("10", "20-98765432-1", "CLIENT");
        String token = jwtService.generateToken(tokenRequestDTO);
        // Act
        TokenDTO tokenDTO = jwtService.authenticateTokenAndGetData(token);
        // Assert
        assertTrue(tokenDTO.isAuthenticated());
        assertEquals("10", tokenDTO.getUserId());
        assertEquals("20-98765432-1", tokenDTO.getUserCuit());
        assertEquals("CLIENT", tokenDTO.getUserRole());
    }

    @Test
    void authenticateTokenAndGetData_shouldReturnNotAuthenticated_whenTokenIsInvalid() {
        // Token totalmente inválido (mal formado)
        String invalidToken = "invalid.token.string";

        TokenDTO tokenDTO = jwtService.authenticateTokenAndGetData(invalidToken);

        assertFalse(tokenDTO.isAuthenticated());
        assertNull(tokenDTO.getUserId());
        assertNull(tokenDTO.getUserCuit());
        assertNull(tokenDTO.getUserRole());
    }

    @Test
    void authenticateTokenAndGetData_shouldReturnNotAuthenticated_whenTokenIsExpired() throws Exception {
        // Configuramos duración de token muy corta
        setPrivateField(jwtService, "TOKEN_DURATION_MINUTES", 0L);

        // Generamos token ya expirado
        // Arrange
        TokenRequestDTO tokenRequestDTO = new TokenRequestDTO("10", "20-98765432-1", "CLIENT");
        String token = jwtService.generateToken(tokenRequestDTO);

        // Esperamos 2 segundos por las dudas
        TimeUnit.SECONDS.sleep(2);

        // Act
        TokenDTO tokenDTO = jwtService.authenticateTokenAndGetData(token);

        // Assert
        assertFalse(tokenDTO.isAuthenticated());
    }
}
