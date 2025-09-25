package com.BilleteraVirtual.accounts.service;


import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public void altaCuenta(CreateAccountDTO createAccountDTO) {
        Account account = new Account();
        account.setEstado(AccountStatus.ACTIVE);
        account.setMoneda(createAccountDTO.getMoneda());
        account.setSaldo(BigDecimal.ZERO);
        account.setUserId(createAccountDTO.getUserId());
        accountRepository.save(account);
    }

    public void bajaCuenta(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) return;
        account.setEstado(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    public List<Account> consultarCuentas(Long user_id) {
        return accountRepository.findAllByUserId(user_id);
    }

    public BigDecimal consultarFondoCuenta(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return null;
        return account.getSaldo();
    }

    public void actualizarSaldo(UpdateAccountDTO updateAccountDTO) {
        Account account = accountRepository.findById(updateAccountDTO.getAccount_id()).orElse(null);
        if (account == null) return;
        account.setSaldo(updateAccountDTO.getNewSaldo());
        accountRepository.save(account);
    }

    @Transactional // TODO optimizar con script transactional directo en BBDD
    public WithdrawResponseDTO executeWithdraw(WithdrawRequestDTO withdrawRequestDTO) {
        String validation = this.consultarCuentaParaTransferencia(withdrawRequestDTO);
        if (!validation.equals("OK")) return new WithdrawResponseDTO(false, "ERROR: " + validation);

        Account accountFrom = accountRepository.findById(withdrawRequestDTO.getAccountFrom()).orElse(null);
        Account accountTo = accountRepository.findById(withdrawRequestDTO.getAccountTo()).orElse(null);

        accountFrom.setSaldo(accountFrom.getSaldo().subtract(withdrawRequestDTO.getAmount()));
        accountTo.setSaldo(accountTo.getSaldo().add(withdrawRequestDTO.getAmount()));

        this.accountRepository.save(accountFrom);
        this.accountRepository.save(accountTo);

        return new WithdrawResponseDTO(true, null);
    }

    public String consultarCuentaParaTransferencia(WithdrawRequestDTO withdrawRequestDTO) {
        // Validation 0: Same Account
        if(withdrawRequestDTO.getAccountFrom() == withdrawRequestDTO.getAccountTo()) return "MISMA CUENTA";
        // DB Search
        Account accountFrom = accountRepository.findById(withdrawRequestDTO.getAccountFrom()).orElse(null);
        Account accountTo = accountRepository.findById(withdrawRequestDTO.getAccountTo()).orElse(null);
        // Validation 1: Accounts exists
        if (accountFrom == null || accountTo == null) return "CUENTA INEXISTENTE";
        // Validation 2: Accounts are active
        if (!accountFrom.getEstado().equals(AccountStatus.ACTIVE)) return "CUENTA NO ACTIVA: " + accountFrom.getEstado();
        if (!accountTo.getEstado().equals(AccountStatus.ACTIVE)) return "CUENTA NO ACTIVA: " + accountTo.getEstado();
        // Validation 3: Account "FROM" Founds Check
        if (accountFrom.getSaldo().compareTo(withdrawRequestDTO.getAmount()) < 0) return "SALDO INSUFICIENTE";
        // Transaction OK
        return "OK";
    }


}
