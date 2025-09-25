package com.billeteraVirtual.users.dto;

import com.billeteraVirtual.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentialsResponseDTO {
    private boolean authenticated;
    private UserDTO userDTO;

}
