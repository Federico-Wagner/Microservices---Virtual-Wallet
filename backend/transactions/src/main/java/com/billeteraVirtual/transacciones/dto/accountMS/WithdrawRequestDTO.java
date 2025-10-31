package com.billeteraVirtual.transacciones.dto.accountMS;


import com.billeteraVirtual.transacciones.dto.TransactionRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawRequestDTO {

    private String token;
    private Long accountFrom;
    private Long accountTo;
    private BigDecimal amount;

    public WithdrawRequestDTO(TransactionRequestDTO transactionRequestDTO) {
        this.setToken(transactionRequestDTO.getToken());
        this.setAccountFrom(transactionRequestDTO.getAccountIdFrom());
        this.setAccountTo(transactionRequestDTO.getAccountIdTo());
        this.setAmount(transactionRequestDTO.getAmount());
    }
}
