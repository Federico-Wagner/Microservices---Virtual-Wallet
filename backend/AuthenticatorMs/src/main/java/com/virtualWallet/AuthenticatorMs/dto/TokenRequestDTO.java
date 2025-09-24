package com.virtualWallet.AuthenticatorMs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequestDTO {
    private String token;
    private String serviceName;
}
