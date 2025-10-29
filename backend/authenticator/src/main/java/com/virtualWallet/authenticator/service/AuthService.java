package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.*;
import com.virtualWallet.authenticator.enumerators.RolesEnum;
import com.virtualWallet.authenticator.enumerators.eService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final ExternalResoursesConnectionService externalResoursesConnectionService;
    @Autowired
    private final PermissionService permissionService;

    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        // Users Ms - validateUserCredentials
        UserCredentialsResponseDTO userCredentialsResponseDTO =
                externalResoursesConnectionService.validateUserCredentials(loginRequestDTO.getUser(), loginRequestDTO.getPassword());
        if (!userCredentialsResponseDTO.isAuthenticated()) {
            return new LoginResponseDTO(false, null, "Invalid Credentials");
        }
        UserDTO userDTO = userCredentialsResponseDTO.getUserDTO();
        String token = jwtService.generateToken(loginRequestDTO.getUser(), userDTO.getRole());
        return new LoginResponseDTO(true, token, null);
    }

    public ResponseEntity<?> validateTokenForAction(TokenRequestDTO tokenRequestDTO) {
        // VALIDATION 1: Execution permits
        eService service = eService.getEnum(tokenRequestDTO.getServiceName());
        RolesEnum role = jwtService.getTokenRole(tokenRequestDTO.getToken());
        if (!permissionService.checkRoleAccessForService(service, role)){
            return ResponseEntity.status(401).body(new TokenResponseDTO(false, null));
        }
        // VALIDATION 2: Token validation
        String userIdentificator = jwtService.validateTokenAndGetUserIdentificator(tokenRequestDTO.getToken());
        if (userIdentificator == null) { // token expirado o invalido
            return ResponseEntity.status(401).body(new TokenResponseDTO(false, null));
        }
        return ResponseEntity.ok(new TokenResponseDTO(true, userIdentificator));
    }

    public String generateToken(String userIdentification, RolesEnum rolesEnum) {
        return jwtService.generateToken(userIdentification, rolesEnum);
    }

}

