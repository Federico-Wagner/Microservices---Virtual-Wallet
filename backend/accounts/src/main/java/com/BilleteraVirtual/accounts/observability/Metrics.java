package com.BilleteraVirtual.accounts.observability;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
public class Metrics {

    private final Timer userAccountsTokenTimer;
    private final Timer withdrawTimer;

    public Metrics(MeterRegistry registry) {
        this.userAccountsTokenTimer = Timer.builder("users.get-user-accounts-token.latency")
                .description("Latency for getUserAccountsToken")
                .publishPercentileHistogram()
                .register(registry);

        this.withdrawTimer = Timer.builder("users.withdraw.latency")
                .description("Latency for withdraw")
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
