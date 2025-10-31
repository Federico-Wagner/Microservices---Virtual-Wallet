package com.BilleteraVirtual.accounts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequestDTO {
    private String userId;
    private String userCuit;
    private String rolesEnum;
}
