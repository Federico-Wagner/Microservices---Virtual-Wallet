package com.BilleteraVirtual.accounts.service;

import com.BilleteraVirtual.accounts.dto.CreateAccountDTO;
import com.BilleteraVirtual.accounts.dto.WithdrawRequestDTO;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.enumerators.CurrencyType;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAltaCuenta() {
        CreateAccountDTO dto = new CreateAccountDTO();
        dto.setUserId(1L);
        dto.setCurrency(CurrencyType.USD);

        accountService.altaCuenta(dto);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();

        assertEquals(AccountStatus.ACTIVE, saved.getState());
        assertEquals(CurrencyType.USD, saved.getCurrency());
        assertEquals(BigDecimal.ZERO, saved.getBalance());
        assertEquals(1L, saved.getUserId());
    }

    @Test
    void testBajaCuenta() {
        Account account = new Account();
        account.setId(1L);
        account.setState(AccountStatus.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.bajaCuenta(1L);

        assertEquals(AccountStatus.CLOSED, account.getState());
        verify(accountRepository).save(account);
    }

    @Test
    void testExecuteWithdraw_OK() {
        Account from = new Account();
        from.setId(1L);
        from.setState(AccountStatus.ACTIVE);
        from.setBalance(BigDecimal.valueOf(1000));

        Account to = new Account();
        to.setId(2L);
        to.setState(AccountStatus.ACTIVE);
        to.setBalance(BigDecimal.valueOf(200));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        WithdrawRequestDTO request = new WithdrawRequestDTO();
        request.setAccountFrom(1L);
        request.setAccountTo(2L);
        request.setAmount(BigDecimal.valueOf(300));

        var response = accountService.executeWithdraw(request);

        assertTrue(response.isSuccess());
        assertEquals(BigDecimal.valueOf(700), from.getBalance());
        assertEquals(BigDecimal.valueOf(500), to.getBalance());

        verify(accountRepository).save(from);
        verify(accountRepository).save(to);
    }

    @Test
    void testConsultarCuentaParaTransferencia_Errores() {
        // Misma cuenta
        WithdrawRequestDTO dto = new WithdrawRequestDTO();
        dto.setAccountFrom(1L);
        dto.setAccountTo(1L);
        dto.setAmount(BigDecimal.valueOf(10));
        assertEquals("MISMA CUENTA", accountService.consultarCuentaParaTransferencia(dto));

        // Cuenta inexistente
        dto.setAccountFrom(1L);
        dto.setAccountTo(2L);
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        when(accountRepository.findById(2L)).thenReturn(Optional.empty());
        assertEquals("CUENTA INEXISTENTE", accountService.consultarCuentaParaTransferencia(dto));

        // Cuenta no activa
        Account from = new Account();
        from.setState(AccountStatus.CLOSED);
        Account to = new Account();
        to.setState(AccountStatus.ACTIVE);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));
        assertEquals("CUENTA NO ACTIVA: CLOSED", accountService.consultarCuentaParaTransferencia(dto));

        // Saldo insuficiente
        from.setState(AccountStatus.ACTIVE);
        from.setBalance(BigDecimal.valueOf(50));
        dto.setAmount(BigDecimal.valueOf(100));
        assertEquals("SALDO INSUFICIENTE", accountService.consultarCuentaParaTransferencia(dto));
    }
}
