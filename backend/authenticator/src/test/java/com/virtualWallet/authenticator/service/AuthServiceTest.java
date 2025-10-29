package com.virtualWallet.authenticator.service;

//import org.junit.jupiter.api.Test;
//
//import static org.junit.jupiter.api.Assertions.*;
//class AuthServiceTest {
//
//    @Test
//    void login() {
//    }
//
//    @Test
//    void validateTokenForAction() {
//    }
//
//    @Test
//    void generateToken() {
//    }
//}


import com.virtualWallet.authenticator.dto.*;
import com.virtualWallet.authenticator.enumerators.RolesEnum;
import com.virtualWallet.authenticator.enumerators.eService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class AuthServiceTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private ExternalResoursesConnectionService externalResoursesConnectionService;
    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // --- LOGIN ---
    @Test
    void login_shouldReturnInvalidCredentials_whenAuthenticationFails() {
        // Arrange
        when(externalResoursesConnectionService.validateUserCredentials("123", "wrong"))
                .thenReturn(new UserCredentialsResponseDTO(false, null));

        // Act
        LoginResponseDTO response = authService.login(new LoginRequestDTO("123", "wrong"));

        // Assert
        assertFalse(response.isAuthenticated());
        assertNull(response.getToken());
        assertEquals("Invalid Credentials", response.getErrorMessage());
    }

    @Test
    void login_shouldReturnToken_whenAuthenticationSucceeds() {
        // Arrange
        UserDTO user = new UserDTO();
        user.setRole(RolesEnum.CLIENT);

        when(externalResoursesConnectionService.validateUserCredentials("123", "pass"))
                .thenReturn(new UserCredentialsResponseDTO(true, user));
        when(jwtService.generateToken("123", RolesEnum.CLIENT)).thenReturn("jwt-token");

        // Act
        LoginResponseDTO response = authService.login(new LoginRequestDTO("123", "pass"));

        // Assert
        assertTrue(response.isAuthenticated());
        assertEquals("jwt-token", response.getToken());
        assertNull(response.getErrorMessage());
    }

    // --- VALIDATE TOKEN FOR ACTION ---
    @Test
    void validateTokenForAction_shouldReturn401_whenRoleNotAllowed() {
        // Arrange
        RolesEnum role = RolesEnum.SUPPORT;
        eService serviceEnum = eService.TRANSACTION;
        TokenRequestDTO request = new TokenRequestDTO("token123", serviceEnum.getId());

        when(jwtService.getTokenRole("token123")).thenReturn(role);
        when(permissionService.checkRoleAccessForService(serviceEnum, role)).thenReturn(false);

        // Act
        ResponseEntity<?> response = authService.validateTokenForAction(request);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        TokenResponseDTO body = (TokenResponseDTO) response.getBody();
        assertNotNull(body);
        assertFalse(body.isAuthenticated());
    }

    @Test
    void validateTokenForAction_shouldReturn401_whenTokenInvalid() {
        // Arrange
        RolesEnum role = RolesEnum.CLIENT;
        eService serviceEnum = eService.TRANSACTION;
        TokenRequestDTO request = new TokenRequestDTO("token123", serviceEnum.getId());

        when(jwtService.getTokenRole("token123")).thenReturn(role);
        when(permissionService.checkRoleAccessForService(serviceEnum, role)).thenReturn(true);
        when(jwtService.validateTokenAndGetUserIdentificator("token123")).thenReturn(null);

        // Act
        ResponseEntity<?> response = authService.validateTokenForAction(request);

        // Assert
        assertEquals(401, response.getStatusCodeValue());
        TokenResponseDTO body = (TokenResponseDTO) response.getBody();
        assertNotNull(body);
        assertFalse(body.isAuthenticated());
    }

    @Test
    void validateTokenForAction_shouldReturnOk_whenValidTokenAndPermission() {
        // Arrange
        RolesEnum role = RolesEnum.CLIENT;
        eService serviceEnum = eService.TRANSACTION;
        TokenRequestDTO request = new TokenRequestDTO("token123", serviceEnum.getId());

        when(jwtService.getTokenRole("token123")).thenReturn(role);
        when(permissionService.checkRoleAccessForService(serviceEnum, role)).thenReturn(true);
        when(jwtService.validateTokenAndGetUserIdentificator("token123")).thenReturn("user123");

        // Act
        ResponseEntity<?> response = authService.validateTokenForAction(request);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        TokenResponseDTO body = (TokenResponseDTO) response.getBody();
        assertNotNull(body);
        assertTrue(body.isAuthenticated());
        assertEquals("user123", body.getUserIdentificator());
    }

}
