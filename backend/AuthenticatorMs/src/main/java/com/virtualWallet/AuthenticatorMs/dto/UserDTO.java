package com.virtualWallet.AuthenticatorMs.dto;


import com.virtualWallet.AuthenticatorMs.enumerators.RolesEnum;
import com.virtualWallet.AuthenticatorMs.enumerators.UserState;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String dni;
    private String password;

    private RolesEnum role;
    private UserState estado;

}
