package com.BilleteraVirtual.accounts.dto;

import lombok.Data;

import java.math.BigDecimal;


@Data
public class UpdateAccountDTO {

    private Long user_id;

    private Long account_id;

    private BigDecimal newSaldo;

}
