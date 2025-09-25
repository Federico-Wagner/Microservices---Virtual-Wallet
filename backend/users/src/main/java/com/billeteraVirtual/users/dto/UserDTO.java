package com.billeteraVirtual.users.dto;

import com.billeteraVirtual.users.enumerators.RolesEnum;
import lombok.Data;

@Data
public class UserDTO {

    private Long id;
    private String name;
    private String surname;
    private String dni;
    private String password;

    private RolesEnum role;

}


