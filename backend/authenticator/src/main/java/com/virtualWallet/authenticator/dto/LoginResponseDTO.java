package com.virtualWallet.authenticator.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {

    private boolean authenticated;
    private String token;
    private String errorMessage;
}
