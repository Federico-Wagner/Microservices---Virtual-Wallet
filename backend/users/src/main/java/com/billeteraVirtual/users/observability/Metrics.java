package com.billeteraVirtual.users.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
public class Metrics {

    private final Counter loginSuccess;
    private final Counter loginFailure;
    private final Timer loginTimer;

    private final Counter signupSuccess;
    private final Counter signUpFailure;
    private final Timer signUpTimer;

    private final Timer getUserDataTokenTimer;

    public Metrics(MeterRegistry registry) {
        this.loginSuccess = Counter.builder("users.login.success")
                .description("Login Success")
                .register(registry);
        this.loginFailure = Counter.builder("users.login.failure")
                .description("Login Failed")
                .register(registry);
        this.loginTimer = Timer.builder("users.login.latency")
                .description("Latency for Login")
                .publishPercentileHistogram()
                .register(registry);

        this.signupSuccess = Counter.builder("users.sign-up.success")
                .description("Sign Up Success")
                .register(registry);
        this.signUpFailure = Counter.builder("users.sign-up.failure")
                .description("Sign Up Failed")
                .register(registry);
        this.signUpTimer = Timer.builder("users.sign-up.latency")
                .description("Latency for Sign Up")
                .publishPercentileHistogram()
                .register(registry);

        this.getUserDataTokenTimer = Timer.builder("users.get-user-data-token.latency")
                .description("Latency for getUserDataToken")
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
            MDC.put("traceId", UUID.randomUUID().toString()); // todo OpenTelemetry
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
