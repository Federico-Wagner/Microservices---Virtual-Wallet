package com.BilleteraVirtual.accounts.service;


import com.BilleteraVirtual.accounts.Mapper.AccountMapper;
import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.util.Utils;
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

    public AccountService(ExternalResoursesConnectionService extResourse,
                          AccountRepository accountRepository, AccountMapper accountMapper) {
        this.extResourse = extResourse;
        this.accountRepository = accountRepository;
        this.accountMapper = accountMapper;
    }


    public ResponseDTO<List<AccountDTO>> getUserAccountsToken(String token) {
        TokenDTO tokenDTO = extResourse.authenticateToken(token);
        if (!tokenDTO.isAuthenticated()) {
            return new ResponseDTO<>(false, "Token expired");
        }
        List<Account> accountList = this.accountRepository.findAllByUserId(Long.valueOf(tokenDTO.getUserId()));
        List<AccountDTO> accountDTOList = accountList.stream()
                .map(accountMapper::toDto)
                .toList();
        return new ResponseDTO<>(true, accountDTOList);
    }

    @Transactional
    public WithdrawResponseDTO executeWithdraw(WithdrawRequestDTO withdrawRequestDTO) {
        String validation = this.consultarCuentaParaTransferencia(withdrawRequestDTO);
        if (!validation.equals("OK")){
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

}
