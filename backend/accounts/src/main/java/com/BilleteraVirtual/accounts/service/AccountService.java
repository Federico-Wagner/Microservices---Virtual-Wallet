package com.BilleteraVirtual.accounts.service;


import com.BilleteraVirtual.accounts.Mapper.AccountMapper;
import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
public class AccountService {

    private final ExternalResoursesConnectionService extResourse;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountService(ExternalResoursesConnectionService extResourse,
                          AccountRepository accountRepository,
                          AccountMapper accountMapper) {
        this.extResourse = extResourse;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }


    public ResponseDTO<List<AccountDTO>> getUserAccountsToken(String token) {
        try {
            TokenDTO tokenDTO = extResourse.authenticateToken(token);
            if (!tokenDTO.isAuthenticated()) {
                log.info("Token invalid or expired");
                return ResponseDTO.failure("Token expired");
            }
            List<Account> accountList = this.accountRepository.findAllByUserId(Long.valueOf(tokenDTO.getUserId()));
            List<AccountDTO> accountDTOList = accountList.stream()
                    .map(accountMapper::toDto)
                    .toList();
            log.info("Success");
            return ResponseDTO.success(accountDTOList);
        } catch (Exception e) {
            log.error("Unexpected error | message={}", e.getMessage(), e);
            return ResponseDTO.failure("Internal server error");
        }
    }


    @Transactional
    public ResponseDTO<?> executeWithdraw(WithdrawRequestDTO withdrawRequestDTO) {
        try {
            String validation = this.consultarCuentaParaTransferencia(withdrawRequestDTO);
            if (!validation.equals("OK")) {
                log.info("CONSULTA_SALDO_CUENTA - {}", validation);
                return ResponseDTO.failure("ERROR: " + validation);
            }
            Account accountFrom = accountRepository.findById(withdrawRequestDTO.getAccountFrom()).orElse(null);
            Account accountTo = accountRepository.findById(withdrawRequestDTO.getAccountTo()).orElse(null);
            accountFrom.setBalance(accountFrom.getBalance().subtract(withdrawRequestDTO.getAmount()));
            accountTo.setBalance(accountTo.getBalance().add(withdrawRequestDTO.getAmount()));
            this.accountRepository.save(accountFrom);
            this.accountRepository.save(accountTo);
            log.info("Withdraw executed");
            return ResponseDTO.success(null);
        } catch (Exception e) {
            log.error("Unexpected error | message={}", e.getMessage(), e);
            return ResponseDTO.failure("Internal server error");
        }
    }

    public String consultarCuentaParaTransferencia(WithdrawRequestDTO withdrawRequestDTO) {
        // Validation 0: Same Account
        if (withdrawRequestDTO.getAccountFrom() == withdrawRequestDTO.getAccountTo()) return "MISMA CUENTA";
        // DB Search
        Account accountFrom = accountRepository.findById(withdrawRequestDTO.getAccountFrom()).orElse(null);
        Account accountTo = accountRepository.findById(withdrawRequestDTO.getAccountTo()).orElse(null);
        // Validation 1: Accounts exists
        if (accountFrom == null || accountTo == null) return "CUENTA INEXISTENTE";
        // Validation 2: Accounts are active
        if (!accountFrom.getState().equals(AccountStatus.ACTIVE))
            return "CUENTA NO ACTIVA: " + accountFrom.getState();
        if (!accountTo.getState().equals(AccountStatus.ACTIVE)) return "CUENTA NO ACTIVA: " + accountTo.getState();
        // Validation 3: Account "FROM" Founds Check
        if (accountFrom.getBalance().compareTo(withdrawRequestDTO.getAmount()) < 0) return "SALDO INSUFICIENTE";
        // Transaction OK
        return "OK";
    }

    public ResponseDTO<?> altaCuenta(CreateAccountDTO createAccountDTO) {
        Account account = new Account();
        account.setState(AccountStatus.ACTIVE);
        account.setCurrency(createAccountDTO.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setUserId(createAccountDTO.getUserId());
        accountRepository.save(account);
        return ResponseDTO.success(null);
    }

    public ResponseDTO<?> bajaCuenta(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) return ResponseDTO.failure("Account not found");
        account.setState(AccountStatus.CLOSED);
        accountRepository.save(account);
        return ResponseDTO.success(null);
    }

}
