package com.virtualWallet.authenticator.controller;

import com.virtualWallet.authenticator.dto.ResponseDTO;
import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import com.virtualWallet.authenticator.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @GetMapping("/test")
    public String test() {
        return "MS Auth works!";
    }

    @PostMapping("/generateToken")
    public ResponseDTO<String> generateToken(@RequestBody TokenRequestDTO tokenRequestDTO) {
        ResponseDTO<String> responseDTO = authService.generateToken(tokenRequestDTO);
        return responseDTO;
    }

    @PostMapping("/authenticateToken")
    public TokenDTO authenticateToken(@RequestBody String token) {
        TokenDTO tokenDTO = authService.authenticateToken(token);
        return tokenDTO;
    }

}
