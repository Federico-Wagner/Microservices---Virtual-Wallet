package com.BilleteraVirtual.accounts.entity;


import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.enumerators.CurrencyType;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.annotation.Profile;


import java.math.BigDecimal;

@Data
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String alias;

    private BigDecimal saldo;

    @Enumerated(EnumType.STRING)
    private CurrencyType moneda;

    @Enumerated(EnumType.STRING)
    private AccountStatus estado;

}
