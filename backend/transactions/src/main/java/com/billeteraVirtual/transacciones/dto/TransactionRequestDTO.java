package com.billeteraVirtual.transacciones.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {

    private String token;
    private Long accountIdFrom;
    private Long accountIdTo;
    private BigDecimal amount;

}
