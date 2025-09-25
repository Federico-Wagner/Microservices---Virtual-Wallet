package com.BilleteraVirtual.accounts.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithdrawResponseDTO {

    private boolean success;
    private String errMsg;

}
