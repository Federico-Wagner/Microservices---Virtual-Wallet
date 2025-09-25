package com.billeteraVirtual.users.entity;



import com.billeteraVirtual.users.enumerators.RolesEnum;
import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String surname;

    private String dni;

    private String password;

    @Enumerated(EnumType.STRING)
    private RolesEnum role;

}
