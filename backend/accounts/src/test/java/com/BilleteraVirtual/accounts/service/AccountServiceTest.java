package com.BilleteraVirtual.accounts.service;

import com.BilleteraVirtual.accounts.dto.*;
import com.BilleteraVirtual.accounts.entity.Account;
import com.BilleteraVirtual.accounts.enumerators.AccountStatus;
import com.BilleteraVirtual.accounts.enumerators.CurrencyType;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        dto.setMoneda(CurrencyType.USD);

        accountService.altaCuenta(dto);

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        verify(accountRepository).save(captor.capture());
        Account saved = captor.getValue();

        assertEquals(AccountStatus.ACTIVE, saved.getEstado());
        assertEquals(CurrencyType.USD, saved.getMoneda());
        assertEquals(BigDecimal.ZERO, saved.getSaldo());
        assertEquals(1L, saved.getUserId());
    }

    @Test
    void testBajaCuenta() {
        Account account = new Account();
        account.setId(1L);
        account.setEstado(AccountStatus.ACTIVE);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        accountService.bajaCuenta(1L);

        assertEquals(AccountStatus.CLOSED, account.getEstado());
        verify(accountRepository).save(account);
    }

    @Test
    void testConsultarCuentas() {
        Account a1 = new Account();
        Account a2 = new Account();
        when(accountRepository.findAllByUserId(1L)).thenReturn(List.of(a1, a2));

        List<Account> accounts = accountService.consultarCuentas(1L);

        assertEquals(2, accounts.size());
    }

    @Test
    void testConsultarFondoCuenta() {
        Account account = new Account();
        account.setSaldo(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        BigDecimal saldo = accountService.consultarFondoCuenta(1L);
        assertEquals(BigDecimal.valueOf(100), saldo);
    }

    @Test
    void testActualizarSaldo() {
        Account account = new Account();
        account.setId(1L);
        account.setSaldo(BigDecimal.ZERO);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));

        UpdateAccountDTO dto = new UpdateAccountDTO();
        dto.setAccount_id(1L);
        dto.setNewSaldo(BigDecimal.valueOf(500));

        accountService.actualizarSaldo(dto);

        assertEquals(BigDecimal.valueOf(500), account.getSaldo());
        verify(accountRepository).save(account);
    }

    @Test
    void testExecuteWithdraw_OK() {
        Account from = new Account();
        from.setId(1L);
        from.setEstado(AccountStatus.ACTIVE);
        from.setSaldo(BigDecimal.valueOf(1000));

        Account to = new Account();
        to.setId(2L);
        to.setEstado(AccountStatus.ACTIVE);
        to.setSaldo(BigDecimal.valueOf(200));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));

        WithdrawRequestDTO request = new WithdrawRequestDTO();
        request.setAccountFrom(1L);
        request.setAccountTo(2L);
        request.setAmount(BigDecimal.valueOf(300));

        var response = accountService.executeWithdraw(request);

        assertTrue(response.isSuccess());
        assertEquals(BigDecimal.valueOf(700), from.getSaldo());
        assertEquals(BigDecimal.valueOf(500), to.getSaldo());

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
        from.setEstado(AccountStatus.CLOSED);
        Account to = new Account();
        to.setEstado(AccountStatus.ACTIVE);
        when(accountRepository.findById(1L)).thenReturn(Optional.of(from));
        when(accountRepository.findById(2L)).thenReturn(Optional.of(to));
        assertEquals("CUENTA NO ACTIVA: CLOSED", accountService.consultarCuentaParaTransferencia(dto));

        // Saldo insuficiente
        from.setEstado(AccountStatus.ACTIVE);
        from.setSaldo(BigDecimal.valueOf(50));
        dto.setAmount(BigDecimal.valueOf(100));
        assertEquals("SALDO INSUFICIENTE", accountService.consultarCuentaParaTransferencia(dto));
    }
}
