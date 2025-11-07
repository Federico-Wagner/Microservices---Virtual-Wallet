package com.billeteraVirtual.transacciones.observability;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Getter
@Component
public class Metrics {

    private final Counter withdrawSuccess;
    private final Counter withdrawFailure;
    private final Timer withdrawTimer;

    private final Timer transactionHistoryTimer;



    public Metrics(MeterRegistry registry) {
        this.withdrawSuccess = Counter.builder("transactions.withdraw.success")
                .description("Withdraw Success")
                .register(registry);
        this.withdrawFailure = Counter.builder("transactions.withdraw.failure")
                .description("Withdraw Failed")
                .register(registry);
        this.withdrawTimer = Timer.builder("transactions.withdraw.latency")
                .description("Latency for withdraw")
                .publishPercentileHistogram()
                .register(registry);

        this.transactionHistoryTimer = Timer.builder("transactions.transaction-history.latency")
                .description("Latency for Transaction History")
                .publishPercentileHistogram()
                .register(registry);

    }

    public MDCContext trace(String operationName) {
        return new MDCContext(operationName);
    }

    // ========================
    // Context MDC
    // ========================
    @Slf4j
    public static class MDCContext implements AutoCloseable {

        public MDCContext(String operationName) {
            MDC.put("traceId", UUID.randomUUID().toString()); // todo OpenTelemetry
            MDC.put("operation", operationName);
        }

        @Override
        public void close() {
            log.info("Process completed");
            MDC.clear();
        }
    }
}
