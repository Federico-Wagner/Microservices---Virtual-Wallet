package com.BilleteraVirtual.accounts.controllers;


import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    @Autowired
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/test")
    public String test() {
        return "MS Accounts works!";
    }

    @GetMapping("/get-user-accounts-token/{token}")
    public ResponseDTO<List<AccountDTO>> getUserAccountsToken(@PathVariable String token) {
        ResponseDTO<List<AccountDTO>> responseDTO = this.accountService.getUserAccountsToken(token);
        return responseDTO;
    }

    @PostMapping("/executeWithdraw")
    public ResponseDTO<?> executeWithdraw(@RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        return accountService.executeWithdraw(withdrawRequestDTO);
    }

    // ALTAS Y BAJAS
    @PostMapping("/alta")
    public ResponseDTO<?> altaCuenta(@RequestBody CreateAccountDTO createAccountDTO) {
        return accountService.altaCuenta(createAccountDTO);
    }

    @PostMapping("/baja/{id}")
    public ResponseDTO<?> bajaCuenta(@PathVariable Long id) {
        return accountService.bajaCuenta(id);
    }

}
