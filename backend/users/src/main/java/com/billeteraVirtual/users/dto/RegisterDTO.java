package com.billeteraVirtual.users.dto;


import lombok.Data;

@Data
public class RegisterDTO {

    private String dni;
    private String name;
    private String surname;
    private String password;

}
