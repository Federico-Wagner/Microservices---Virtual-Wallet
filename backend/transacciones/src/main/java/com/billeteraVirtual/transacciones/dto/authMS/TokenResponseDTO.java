package com.billeteraVirtual.transacciones.dto.authMS;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponseDTO {
    private boolean authenticated;
    private String userIdentificator;
}