package com.billeteraVirtual.transacciones.controllers;



import com.billeteraVirtual.transacciones.NotificationPublisher;
import com.billeteraVirtual.transacciones.dto.ResponseDTO;
import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import com.billeteraVirtual.transacciones.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ThreadLocalRandom;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    TransactionService transactionService;

    private final NotificationPublisher publisher;

    public TransactionController(TransactionService transactionService,
                                 NotificationPublisher publisher) {
        this.transactionService = transactionService;
        this.publisher = publisher;
    }

    @PostMapping("/transfer")
    public ResponseEntity<ResponseDTO<?>> transfer(@RequestBody TransactionDTO transactionDTO) {
        long startTime = System.currentTimeMillis();

        ResponseDTO<?> responseDTO = this.transactionService.transfer(transactionDTO);

        waitRandomMiliSeconds(2000, 5000);
        log.info("GET_USER_DATA", kv("duration_ms", calculateDuration(startTime)));
        return ResponseEntity.ok(responseDTO);
    }


    // Va en un logs util
    private long calculateDuration(long startTime) {
        long endTime = System.currentTimeMillis();
        return endTime - startTime;
    }

    // Develop utils
    public static void waitRandomMiliSeconds(int min, int max) {
        int milisegundos = ThreadLocalRandom.current().nextInt(min, max);
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    // TEST
    @GetMapping("/test-send")
    public String testSendRabbitMQ() {
        publisher.sendNotification("testEmail@test.com", "Hi!", "Test message");
        return "Message sent!";
    }

}
