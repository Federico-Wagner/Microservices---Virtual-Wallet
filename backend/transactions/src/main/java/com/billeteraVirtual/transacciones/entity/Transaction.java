package com.billeteraVirtual.transacciones.entity;


import com.billeteraVirtual.transacciones.enumerators.TransactionState;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountIdFrom;

    private Long accountIdTo;

    private BigDecimal amount;

    private String transactionCheck;

    @Enumerated(EnumType.STRING)
    private TransactionState transactionState;

}
