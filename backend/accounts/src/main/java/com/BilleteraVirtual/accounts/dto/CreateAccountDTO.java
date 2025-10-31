package com.BilleteraVirtual.accounts.dto;

import com.BilleteraVirtual.accounts.enumerators.CurrencyType;
import lombok.Data;

@Data
public class CreateAccountDTO {

    private Long userId;
    private CurrencyType currency;

}
