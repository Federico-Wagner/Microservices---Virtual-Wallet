package com.virtualWallet.authenticator.controller;

import com.virtualWallet.authenticator.dto.LoginRequestDTO;
import com.virtualWallet.authenticator.dto.LoginResponseDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import com.virtualWallet.authenticator.enumerators.RolesEnum;
import com.virtualWallet.authenticator.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    @GetMapping("/test")
    public String test(){
        return "MS Auth works!";
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return authService.login(loginRequestDTO);
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
