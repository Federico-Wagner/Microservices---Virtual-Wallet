package com.virtualWallet.AuthenticatorMs.controller;

import com.virtualWallet.AuthenticatorMs.dto.LoginResponseDTO;
import com.virtualWallet.AuthenticatorMs.dto.TokenRequestDTO;
import com.virtualWallet.AuthenticatorMs.enumerators.RolesEnum;
import com.virtualWallet.AuthenticatorMs.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public LoginResponseDTO login(@RequestParam String username, @RequestParam String password) {
        return authService.login(username, password);
    }

    // Validate token api
    @PostMapping("/validateTokenForAction")
    public ResponseEntity<?> validateTokenForAction(@RequestBody TokenRequestDTO tokenRequestDTO) {
        return authService.validateTokenForAction(tokenRequestDTO);
    }

    @PostMapping("/generateToken")
    public String generateToken(@RequestParam String userIdentification,
                                @RequestParam RolesEnum rolesEnum) {
        return authService.generateToken(userIdentification, rolesEnum);
    }

}
