package com.billeteraVirtual.transacciones.dto.accountMS;


import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawRequestDTO {

    private Long accountFrom;
    private Long accountTo;
    private BigDecimal amount;

    public WithdrawRequestDTO(TransactionDTO transactionDTO) {
        this.setAccountFrom(transactionDTO.getAccountFrom());
        this.setAccountTo(transactionDTO.getAccountTo());
        this.setAmount(transactionDTO.getAmount());
    }
}
