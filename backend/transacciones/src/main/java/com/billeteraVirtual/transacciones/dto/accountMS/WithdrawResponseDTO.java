package com.billeteraVirtual.transacciones.dto.accountMS;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class WithdrawResponseDTO {

    private boolean success;
    private String errMsg;

}
