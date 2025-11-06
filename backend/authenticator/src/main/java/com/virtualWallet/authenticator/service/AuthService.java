package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.ResponseDTO;
import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import com.virtualWallet.authenticator.observability.AuthMetrics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final JwtService jwtService;
    private final AuthMetrics metrics;

    public AuthService(JwtService jwtService, AuthMetrics metrics) {
        this.jwtService = jwtService;
        this.metrics = metrics;
    }


    public ResponseDTO<String> generateToken(TokenRequestDTO tokenRequestDTO) {
        try (var ignored = metrics.trace("generateToken", tokenRequestDTO.getUserId())) {
            return metrics.getGenerateTokenTimer().record(() -> {
                try {
                    String token = jwtService.generateToken(tokenRequestDTO);
                    metrics.getGenerateTokenSuccess().increment();
                    log.info("TOKEN GENERATION: SUCCESS");
                    return ResponseDTO.success(token);
                } catch (Exception e) {
                    metrics.getGenerateTokenFailure().increment();
                    log.error("TOKEN GENERATION: FAILED", e);
                    return ResponseDTO.failure("Error generating token");
                }
            });
        }
    }

    public TokenDTO authenticateToken(String token) {
        try (var ignored = metrics.trace("authenticateToken")) {
            return metrics.getAuthenticateTokenTimer().record(() -> {
                TokenDTO tokenDTO = jwtService.authenticateTokenAndGetData(token);
                if (tokenDTO.isAuthenticated()) {
                    metrics.getAuthenticateTokenSuccess().increment();
                    log.info("TOKEN AUTH: True");
                } else {
                    metrics.getAuthenticateTokenFailure().increment();
                    log.info("TOKEN AUTH: False");
                }
                return tokenDTO;
            });
        }
    }

}

