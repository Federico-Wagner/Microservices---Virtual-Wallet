package com.billeteraVirtual.transacciones.entity;


import com.billeteraVirtual.transacciones.enumerators.TransactionState;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private LocalDateTime timeStamp;

    private Long accountIdFrom;

    private Long accountIdTo;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

}
