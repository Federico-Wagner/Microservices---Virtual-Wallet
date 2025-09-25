package com.BilleteraVirtual.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WithdrawRequestDTO {

    private Long accountFrom;
    private Long accountTo;
    private BigDecimal amount;

}
