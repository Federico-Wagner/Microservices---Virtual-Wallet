package com.billeteraVirtual.transacciones.service;


import com.billeteraVirtual.transacciones.Mapper.TransactionMapper;
import com.billeteraVirtual.transacciones.dto.ResponseDTO;
import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import com.billeteraVirtual.transacciones.dto.TransactionRequestDTO;
import com.billeteraVirtual.transacciones.observability.Metrics;
import com.billeteraVirtual.transacciones.repository.TransactionRepository;
import com.billeteraVirtual.transacciones.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;


@Slf4j
@Service
@Primary
public class TransactionServiceObservabilityLayer extends TransactionService {

    private final Metrics metrics;

    public TransactionServiceObservabilityLayer(ExternalResoursesConnectionService extResourse,
                                                TransactionRepository transactionRepository,
                                                TransactionMapper transactionMapper,
                                                Metrics metrics) {
        super(extResourse, transactionRepository, transactionMapper);
        this.metrics = metrics;
    }


    @Override
    public ResponseDTO<?> withdraw(TransactionRequestDTO transactionRequestDTO) {
        try (var ignored = metrics.trace("WITHDRAW")) {
            log.info("Start process | transactionRequestDTO={}", transactionRequestDTO);
            return metrics.getWithdrawTimer().record(() -> {
                ResponseDTO<?> response = super.withdraw(transactionRequestDTO);
                if (response.isSuccess()) this.metrics.getWithdrawSuccess().increment();
                else this.metrics.getWithdrawFailure().increment();
                return response;
            });
        }
    }

    @Override
    public ResponseDTO<List<TransactionDTO>> getTransactionHistory(String token) {
        try (var ignored = metrics.trace("TRANSACTION_HISTORY")) {
            log.info("Start process | token={}", Utils.maskToken(token));
            return metrics.getTransactionHistoryTimer()
                    .record(() -> super.getTransactionHistory(token));
        }
    }

}
