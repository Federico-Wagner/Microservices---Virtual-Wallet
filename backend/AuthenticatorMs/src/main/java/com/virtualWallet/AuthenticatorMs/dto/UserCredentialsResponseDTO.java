package com.virtualWallet.AuthenticatorMs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserCredentialsResponseDTO {

    private boolean authenticated;
    private UserDTO userDTO;

}
