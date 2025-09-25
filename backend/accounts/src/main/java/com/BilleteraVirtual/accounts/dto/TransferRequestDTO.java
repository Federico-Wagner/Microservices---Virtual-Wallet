package com.BilleteraVirtual.accounts.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequestDTO {

    private Long accountFrom;
    private Long accountTo;
    private BigDecimal amount;

}
