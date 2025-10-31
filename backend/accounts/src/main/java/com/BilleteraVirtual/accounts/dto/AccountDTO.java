package com.BilleteraVirtual.accounts.dto;

import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.enumerators.CurrencyType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {

    private Long id;
    private Long userId;
    private String alias;
    private BigDecimal balance;
    private CurrencyType currency;
    private AccountStatus state;

}
