package com.virtualWallet.authenticator.dto;


import com.virtualWallet.authenticator.enumerators.RolesEnum;
import com.virtualWallet.authenticator.enumerators.UserState;
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
