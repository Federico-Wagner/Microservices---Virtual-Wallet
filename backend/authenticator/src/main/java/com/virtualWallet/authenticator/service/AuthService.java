package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.ResponseDTO;
import com.virtualWallet.authenticator.dto.TokenDTO;
import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final JwtService jwtService;

    // token generation
    private final Counter generateTokenSuccessCounter;
    private final Counter generateTokenFailureCounter;
    private final Timer generateTokenTimer;
    // token authentification
    private final Counter authenticateTokenSuccessCounter;
    private final Counter authenticateTokenFailureCounter;
    private final Timer authenticateTokenTimer;

    public AuthService(JwtService jwtService, MeterRegistry registry) {
        this.jwtService = jwtService;

        this.generateTokenSuccessCounter = Counter.builder("token_gen_success_total")
                .description("Total successful token generations")
                .register(registry);
        this.generateTokenFailureCounter = Counter.builder("token_gen_failure_total")
                .description("Total failed token generations")
                .register(registry);
        this.generateTokenTimer = Timer.builder("token_gen_latency")
                .description("token generation latency")
                .publishPercentileHistogram()
                .register(registry);

        this.authenticateTokenSuccessCounter = Counter.builder("token_auth_success_total")
                .description("Total successful token authentications")
                .register(registry);
        this.authenticateTokenFailureCounter = Counter.builder("token_auth_failure_total")
                .description("Total failed token authentications")
                .register(registry);
        this.authenticateTokenTimer = Timer.builder("token_auth_latency")
                .description("token authentification latency")
                .publishPercentileHistogram()
                .register(registry);
    }


    public ResponseDTO<String> generateToken(TokenRequestDTO tokenRequestDTO) {
        return generateTokenTimer.record(() -> {
            try {
                final String token = jwtService.generateToken(tokenRequestDTO);
                log.info("TOKEN GENERATION: Success");
                this.generateTokenSuccessCounter.increment();
                return ResponseDTO.success(token);
            } catch (Exception e) {
                this.generateTokenFailureCounter.increment();
                log.error("TOKEN GENERATION: Failed", e);
                return ResponseDTO.failure("Error generating token");
            }
        });
    }

    public TokenDTO authenticateToken(String token) {
        return authenticateTokenTimer.record(() -> {
            TokenDTO tokenDTO = jwtService.authenticateTokenAndGetData(token);
            log.info("TOCKEN: AUTHENTICATION - {}", tokenDTO.isAuthenticated());
            if (tokenDTO.isAuthenticated()) this.authenticateTokenSuccessCounter.increment();
            else this.authenticateTokenFailureCounter.increment();
            return tokenDTO;
        });
    }

}

