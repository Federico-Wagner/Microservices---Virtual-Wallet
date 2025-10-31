package com.billeteraVirtual.transacciones.dto;

import com.billeteraVirtual.transacciones.enumerators.TransactionState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionDTO {


    private LocalDateTime timeStamp;

    private Long accountIdFrom;

    private Long accountIdTo;

    private BigDecimal amount;

    private TransactionState transactionState;
}
