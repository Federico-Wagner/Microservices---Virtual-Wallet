package com.virtualWallet.authenticator.observability;

import com.virtualWallet.authenticator.dto.TokenRequestDTO;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
public class AuthMetrics {

    private final Counter generateTokenSuccess;
    private final Counter generateTokenFailure;
    private final Timer generateTokenTimer;

    private final Counter authenticateTokenSuccess;
    private final Counter authenticateTokenFailure;
    private final Timer authenticateTokenTimer;

    public AuthMetrics(MeterRegistry registry) {
        this.generateTokenSuccess = Counter.builder("auth.token.gen.success")
                .description("Successful token generations")
                .register(registry);
        this.generateTokenFailure = Counter.builder("auth.token.gen.failure")
                .description("Failed token generations")
                .register(registry);
        this.generateTokenTimer = Timer.builder("auth.token.gen.latency")
                .description("Latency for token generation")
                .publishPercentileHistogram()
                .register(registry);

        this.authenticateTokenSuccess = Counter.builder("auth.token.auth.success")
                .description("Successful authentications")
                .register(registry);
        this.authenticateTokenFailure = Counter.builder("auth.token.auth.failure")
                .description("Failed authentications")
                .register(registry);
        this.authenticateTokenTimer = Timer.builder("auth.token.auth.latency")
                .description("Latency for authentication")
                .publishPercentileHistogram()
                .register(registry);
    }


    public MDCContext trace(String operationName) {
        return new MDCContext(operationName, null);
    }

    public MDCContext trace(String operationName, String userId) {
        return new MDCContext(operationName, userId);
    }

    // ========================
    // Context MDC
    // ========================
    public static class MDCContext implements AutoCloseable {
        public MDCContext(String operationName, String userId) {
            MDC.put("traceId", UUID.randomUUID().toString());
            MDC.put("operation", operationName);
            if (userId != null) {
                MDC.put("userId", userId);
            }
        }

        @Override
        public void close() {
            MDC.clear();
        }
    }
}
