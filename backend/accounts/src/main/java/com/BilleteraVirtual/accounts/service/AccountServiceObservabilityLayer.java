package com.BilleteraVirtual.accounts.service;


import com.BilleteraVirtual.accounts.Mapper.AccountMapper;
import com.BilleteraVirtual.accounts.dto.AccountDTO;
import com.BilleteraVirtual.accounts.dto.ResponseDTO;
import com.BilleteraVirtual.accounts.dto.WithdrawRequestDTO;
import com.BilleteraVirtual.accounts.observability.Metrics;
import com.BilleteraVirtual.accounts.repository.AccountRepository;
import com.BilleteraVirtual.accounts.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@Primary
public class AccountServiceObservabilityLayer extends AccountService {

    private final Metrics metrics;

    private final String END_LOG_TEXT = "Process completed";

    public AccountServiceObservabilityLayer(ExternalResoursesConnectionService extResourse,
                                            AccountRepository accountRepository,
                                            AccountMapper accountMapper,
                                            Metrics metrics) {
        super(extResourse, accountRepository, accountMapper);
        this.metrics = metrics;
    }


    @Override
    public ResponseDTO<List<AccountDTO>> getUserAccountsToken(String token) {
        try (var ignored = metrics.trace("GET_USER_ACCOUNTS_TOKEN")) {
            log.info("Start process | token={}", Utils.maskToken(token));
            return metrics.getUserAccountsTokenTimer()
                    .record(() -> super.getUserAccountsToken(token));
        } finally {
            log.info(END_LOG_TEXT);
        }
    }

    @Override
    public ResponseDTO<?> executeWithdraw(WithdrawRequestDTO withdrawRequestDTO) {
        try (var ignored = metrics.trace("WITHDRAW")) {
            log.info("Start process | from:{} to:{}", withdrawRequestDTO.getAccountFrom(), withdrawRequestDTO.getAccountTo());
            return metrics.getUserAccountsTokenTimer()
                    .record(() -> super.executeWithdraw(withdrawRequestDTO));
        } finally {
            log.info(END_LOG_TEXT);
        }
    }

}
