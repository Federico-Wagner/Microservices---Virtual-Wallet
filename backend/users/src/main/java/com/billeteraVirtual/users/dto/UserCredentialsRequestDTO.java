package com.billeteraVirtual.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentialsRequestDTO {
    private String dni;
    private String password;
}
