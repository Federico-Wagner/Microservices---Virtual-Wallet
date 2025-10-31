package com.virtualWallet.authenticator.controller;

import com.virtualWallet.authenticator.dto.ResponseDTO;
import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import com.virtualWallet.authenticator.service.AuthService;
import com.virtualWallet.authenticator.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.kv;

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
        long startTime = System.currentTimeMillis();
        ResponseDTO<String> responseDTO = authService.generateToken(tokenRequestDTO);
//        Utils.waitRandomMiliSeconds(1000, 2000);
        log.info("TOKEN_GEN", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

    @PostMapping("/authenticateToken")
    public TokenDTO authenticateToken(@RequestBody String token) {
        long startTime = System.currentTimeMillis();
        TokenDTO tokenDTO = authService.authenticateToken(token);
        log.info("TOKEN_AUTH", kv("duration_ms", Utils.calculateDuration(startTime)));
        return tokenDTO;
    }

}
