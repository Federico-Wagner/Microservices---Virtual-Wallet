package com.billeteraVirtual.transacciones.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionDTO {

    private Long accountFrom;
    private Long accountTo;
    private BigDecimal amount;
    private String token;

}
