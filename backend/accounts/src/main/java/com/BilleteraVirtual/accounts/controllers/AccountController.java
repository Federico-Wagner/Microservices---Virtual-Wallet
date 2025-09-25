package com.BilleteraVirtual.accounts.controllers;


import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.service.AccountService;
import com.BilleteraVirtual.accounts.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

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

//     ALTAS Y BAJAS

    @PostMapping("/alta")
    public void altaCuenta(@RequestBody CreateAccountDTO createAccountDTO){
        long startTime = System.currentTimeMillis();

        accountService.altaCuenta(createAccountDTO);

        Utils.waitRandomMiliSeconds(1000, 3000);
        log.info("ALTA_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
    }

    @PostMapping("/baja/{id}")
    public void bajaCuenta(@PathVariable Long id){

        long startTime = System.currentTimeMillis();

        accountService.bajaCuenta(id);

        Utils.waitRandomMiliSeconds(500, 2000);
        log.info("BAJA_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
    }

//     CONSULTAS

    @PostMapping("/consultarCuentas/{user_id}")
    public List<Account> consultarCuentas(@PathVariable Long user_id){
        long startTime = System.currentTimeMillis();

        List<Account> userAccountList = accountService.consultarCuentas(user_id);

        Utils.waitRandomMiliSeconds(2000, 5000);
        log.info("CONSULTA_CUENTAS", kv("duration_ms", Utils.calculateDuration(startTime)));
        return userAccountList;
    }

    @PostMapping("/consultarFondoCuenta/{accountId}")
    public Object consultarFondoCuenta(@PathVariable Long accountId){
        long startTime = System.currentTimeMillis();

        Object response = accountService.consultarFondoCuenta(accountId);

        Utils.waitRandomMiliSeconds(1000, 1500);
        log.info("CONSULTA_SALDO_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
        return response;
    }

//    @PostMapping("/consultarCuentasParaTransferencia")
//    public Object consultarCuentaParaTransferencia(@RequestBody TransferRequestDTO transferRequestDTO){
//        long startTime = System.currentTimeMillis();
//
//        Object response = accountService.consultarCuentaParaTransferencia(transferRequestDTO);
//
//        waitRandomMiliSeconds(1000, 1500);
//        log.info("CONSULTA_SALDO_CUENTA", kv("duration_ms", calculateDuration(startTime)));
//        return response;
//    }

    @PostMapping("/executeWithdraw")
    public WithdrawResponseDTO executeWithdraw(@RequestBody WithdrawRequestDTO withdrawRequestDTO){
        long startTime = System.currentTimeMillis();

        WithdrawResponseDTO withdrawResponseDTO = accountService.executeWithdraw(withdrawRequestDTO);

        Utils.waitRandomMiliSeconds(1000, 1500);
        log.info("CONSULTA_SALDO_CUENTA", kv("duration_ms", Utils.calculateDuration(startTime)));
        return withdrawResponseDTO;
    }


    // ACTUALIZACIONES

    @PostMapping("/actualizarSaldo")
    public void actualizarSaldo(@RequestBody UpdateAccountDTO updateAccountDTO){
        long startTime = System.currentTimeMillis();

        accountService.actualizarSaldo(updateAccountDTO);

        Utils.waitRandomMiliSeconds(4000, 6000);
        log.info("UPDATE_SALDO", kv("duration_ms", Utils.calculateDuration(startTime)));
    }

}
