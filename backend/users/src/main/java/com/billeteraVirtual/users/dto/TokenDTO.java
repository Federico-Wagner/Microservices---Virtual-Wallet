package com.billeteraVirtual.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenDTO {

    public TokenDTO(String token) {
        this.token = token;
    }

    private String token;
    private boolean authenticated;
    private String userId;
    private String userCuit;
    private String userRole;
}
