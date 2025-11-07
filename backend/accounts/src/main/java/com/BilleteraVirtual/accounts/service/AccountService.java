package com.BilleteraVirtual.accounts.service;


import com.BilleteraVirtual.accounts.Mapper.AccountMapper;
import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.observability.Metrics;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@Service
public class AccountService {

    private final ExternalResoursesConnectionService extResourse;
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private final Metrics metrics;

    public AccountService(ExternalResoursesConnectionService extResourse,
                          AccountRepository accountRepository,
                          AccountMapper accountMapper,
                          Metrics metrics) {
        this.extResourse = extResourse;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
        this.metrics = metrics;
    }


    public ResponseDTO<List<AccountDTO>> getUserAccountsToken(String token) {
        try (var ignored = metrics.trace("GET_USER_ACCOUNTS_TOKEN")) {
            log.info("Start process | token={}", maskToken(token));
            return metrics.getUserAccountsTokenTimer().record(() -> {
                try {
                    TokenDTO tokenDTO = extResourse.authenticateToken(token);
                    if (!tokenDTO.isAuthenticated()) {
                        log.info("Token invalid or expired");
                        return new ResponseDTO<>(false, "Token expired");
                    }
                    List<Account> accountList = this.accountRepository.findAllByUserId(Long.valueOf(tokenDTO.getUserId()));
                    List<AccountDTO> accountDTOList = accountList.stream()
                            .map(accountMapper::toDto)
                            .toList();
                    log.info("Success");
                    return new ResponseDTO<>(true, accountDTOList);
                } catch (Exception e) {
                    log.error("Unexpected error | message={}", e.getMessage(), e);
                    return new ResponseDTO<>(false, "Internal server error");
                } finally {
                    log.debug("Process completed");
                }
            });
        }
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(WithdrawRequestDTO withdrawRequestDTO) {
        try (var ignored = metrics.trace("WITHDRAW")) {
            log.info("Start process | from:{} to:{}", withdrawRequestDTO.getAccountFrom(), withdrawRequestDTO.getAccountTo());
            return (WithdrawResponseDTO) metrics.getWithdrawTimer().record(() -> {
                try {
                    String validation = this.consultarCuentaParaTransferencia(withdrawRequestDTO);
                    if (!validation.equals("OK")) {
                        log.info("CONSULTA_SALDO_CUENTA", kv("error", validation));
                        return new WithdrawResponseDTO(false, "ERROR: " + validation);
                    }
                    Account accountFrom = accountRepository.findById(withdrawRequestDTO.getAccountFrom()).orElse(null);
                    Account accountTo = accountRepository.findById(withdrawRequestDTO.getAccountTo()).orElse(null);
                    accountFrom.setBalance(accountFrom.getBalance().subtract(withdrawRequestDTO.getAmount()));
                    accountTo.setBalance(accountTo.getBalance().add(withdrawRequestDTO.getAmount()));
                    this.accountRepository.save(accountFrom);
                    this.accountRepository.save(accountTo);
                    return new WithdrawResponseDTO(true, null);
                } catch (Exception e) {
                    log.error("Unexpected error | message={}", e.getMessage(), e);
                    return new ResponseDTO<>(false, "Internal server error");
                } finally {
                    log.debug("Process completed");
                }
            });
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


    public void altaCuenta(CreateAccountDTO createAccountDTO) {
        Account account = new Account();
        account.setState(AccountStatus.ACTIVE);
        account.setCurrency(createAccountDTO.getCurrency());
        account.setBalance(BigDecimal.ZERO);
        account.setUserId(createAccountDTO.getUserId());
        accountRepository.save(account);
    }

    public void bajaCuenta(Long id) {
        Account account = accountRepository.findById(id).orElse(null);
        if (account == null) return;
        account.setState(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    public List<Account> consultarCuentas(Long user_id) {
        return accountRepository.findAllByUserId(user_id);
    }

    public BigDecimal consultarFondoCuenta(Long accountId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) return null;
        return account.getBalance();
    }

    public void actualizarSaldo(UpdateAccountDTO updateAccountDTO) {
        Account account = accountRepository.findById(updateAccountDTO.getAccount_id()).orElse(null);
        if (account == null) return;
        account.setBalance(updateAccountDTO.getNewSaldo());
        accountRepository.save(account);
    }


    private String maskToken(String token) {
        if (token == null || token.length() < 10) return "****";
        return token.substring(0, 5) + "*****" + token.substring(token.length() - 3);
    }

}
