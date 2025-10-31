package com.BilleteraVirtual.accounts.controllers;


import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.service.AccountService;
import com.BilleteraVirtual.accounts.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

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
        long startTime = System.currentTimeMillis();
        ResponseDTO<List<AccountDTO>> responseDTO = this.accountService.getUserAccountsToken(token);
//        Utils.waitRandomMiliSeconds(1000, 2000);
        log.info("GET_USER_ACCOUNTS_TOKEN", kv("duration_ms", Utils.calculateDuration(startTime)));
        return responseDTO;
    }

    @PostMapping("/executeWithdraw")
    public WithdrawResponseDTO executeWithdraw(@RequestBody WithdrawRequestDTO withdrawRequestDTO) {
        long startTime = System.currentTimeMillis();
        WithdrawResponseDTO withdrawResponseDTO = accountService.executeWithdraw(withdrawRequestDTO);
//        Utils.waitRandomMiliSeconds(1000, 1500);
        log.info("CONSULTA_SALDO_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
        return withdrawResponseDTO;
    }

    // ALTAS Y BAJAS
    @PostMapping("/alta")
    public void altaCuenta(@RequestBody CreateAccountDTO createAccountDTO) {
        long startTime = System.currentTimeMillis();
        accountService.altaCuenta(createAccountDTO);
//        Utils.waitRandomMiliSeconds(1000, 3000);
        log.info("ALTA_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
    }

    @PostMapping("/baja/{id}")
    public void bajaCuenta(@PathVariable Long id) {
        long startTime = System.currentTimeMillis();
        accountService.bajaCuenta(id);
//        Utils.waitRandomMiliSeconds(500, 2000);
        log.info("BAJA_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
    }

}
