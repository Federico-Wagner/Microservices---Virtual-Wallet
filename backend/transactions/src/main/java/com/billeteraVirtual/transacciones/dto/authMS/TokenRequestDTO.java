package com.billeteraVirtual.transacciones.dto.authMS;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRequestDTO {
    private String token;
    private String serviceName;
}
