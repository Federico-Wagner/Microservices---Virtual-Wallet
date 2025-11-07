package com.billeteraVirtual.transacciones.dto;

import com.billeteraVirtual.transacciones.util.Utils;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransactionRequestDTO {

    private String token;
    private Long accountIdFrom;
    private Long accountIdTo;
    private BigDecimal amount;


    @Override
    public String toString() {
        return "TransactionRequestDTO{" +
                "token='" + Utils.maskToken(token) + '\'' +
                ", accountIdFrom=" + accountIdFrom +
                ", accountIdTo=" + accountIdTo +
                ", amount=" + amount +
                '}';
    }

}
