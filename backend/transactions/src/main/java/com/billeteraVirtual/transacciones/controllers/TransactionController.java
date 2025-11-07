package com.billeteraVirtual.transacciones.controllers;


import com.billeteraVirtual.transacciones.NotificationPublisher;
import com.billeteraVirtual.transacciones.dto.ResponseDTO;
import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import com.billeteraVirtual.transacciones.dto.TransactionRequestDTO;
import com.billeteraVirtual.transacciones.service.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        ResponseDTO<List<TransactionDTO>> responseDTO = this.transactionService.getTransactionHistory(token);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ResponseDTO<?>> withdraw(@RequestBody TransactionRequestDTO transactionRequestDTO) {
        ResponseDTO<?> responseDTO = this.transactionService.withdraw(transactionRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

}
