package com.billeteraVirtual.transacciones.controllers;



import com.billeteraVirtual.transacciones.NotificationPublisher;
import com.billeteraVirtual.transacciones.dto.ResponseDTO;
import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import com.billeteraVirtual.transacciones.dto.TransactionRequestDTO;
import com.billeteraVirtual.transacciones.service.TransactionService;
import com.billeteraVirtual.transacciones.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/test")
    public String test(){
        return "MS Transactions works!";
    }

    @GetMapping("/test-send-notification")
    public String testSendRabbitMQ() {
        publisher.sendNotification("testEmail@test.com", "Hi!", "Test message");
        return "Message sent!";
    }

    @PostMapping("/get-transactions-history")
    public ResponseEntity<ResponseDTO<List<TransactionDTO>>> getTransactionHistory(@RequestBody String token){
        long startTime = System.currentTimeMillis();
        ResponseDTO<List<TransactionDTO>> responseDTO = this.transactionService.getTransactionHistory(token);
//        Utils.waitRandomMiliSeconds(2000, 5000);
        log.info("TRANSACTION_HISTORY", kv("duration_ms", Utils.calculateDuration(startTime)));
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ResponseDTO<?>> withdraw(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        long startTime = System.currentTimeMillis();
        ResponseDTO<?> responseDTO = this.transactionService.withdraw(transactionRequestDTO);
//        Utils.waitRandomMiliSeconds(2000, 5000);
        log.info("WITHDRAW", kv("duration_ms", Utils.calculateDuration(startTime)));
        return ResponseEntity.ok(responseDTO);
    }

}
