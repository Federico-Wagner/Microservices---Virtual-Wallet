package com.billeteraVirtual.transacciones.service;


import com.billeteraVirtual.transacciones.Mapper.TransactionMapper;
import com.billeteraVirtual.transacciones.dto.ResponseDTO;
import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import com.billeteraVirtual.transacciones.dto.TransactionRequestDTO;
import com.billeteraVirtual.transacciones.dto.accountMS.WithdrawRequestDTO;
import com.billeteraVirtual.transacciones.dto.accountMS.WithdrawResponseDTO;
import com.billeteraVirtual.transacciones.dto.authMS.TokenDTO;
import com.billeteraVirtual.transacciones.entity.Transaction;
import com.billeteraVirtual.transacciones.enumerators.TransactionState;
import com.billeteraVirtual.transacciones.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class TransactionService {


    private final ExternalResoursesConnectionService externalResoursesConnectionService;

    private final TransactionRepository transactionRepository;

    private final TransactionMapper transactionMapper;

    public TransactionService(ExternalResoursesConnectionService externalResoursesConnectionService,
                              TransactionRepository transactionRepository, TransactionMapper transactionMapper) {
        this.externalResoursesConnectionService = externalResoursesConnectionService;
        this.transactionRepository = transactionRepository;
        this.transactionMapper = transactionMapper;
    }


    public ResponseDTO<?> withdraw(TransactionRequestDTO transactionRequestDTO) {
        // validate token
        TokenDTO tokenDTO = externalResoursesConnectionService.authenticateToken(transactionRequestDTO.getToken());
        if (!tokenDTO.isAuthenticated()) {
            return new ResponseDTO<>(false, "token expired");
        }
        // Register Transaction
        Transaction transaction = new Transaction();
        transaction.setUserId(Long.valueOf(tokenDTO.getUserId()));
        transaction.setAccountIdFrom(transactionRequestDTO.getAccountIdFrom());
        transaction.setAccountIdTo(transactionRequestDTO.getAccountIdTo());
        transaction.setAmount(transactionRequestDTO.getAmount());
        transaction.setTransactionState(TransactionState.PENDING);
        Transaction transactionSAVED = this.transactionRepository.save(transaction);
        // Execute Withdraw
        WithdrawRequestDTO withdrawRequestDTO = new WithdrawRequestDTO(transactionRequestDTO);
        WithdrawResponseDTO withdrawResponseDTO = this.externalResoursesConnectionService.executeWithdraw(withdrawRequestDTO);

        // update transaction state
        if (!withdrawResponseDTO.isSuccess()) {
            transactionSAVED.setTransactionState(TransactionState.REJECTED);
            this.transactionRepository.save(transactionSAVED);
            return new ResponseDTO<>(false, "ERROR: " + withdrawResponseDTO.getErrMsg());
        }
        transactionSAVED.setTransactionState(TransactionState.DONE);
        transactionSAVED = this.transactionRepository.save(transactionSAVED);
        log.info("WITHDRAW_COMPLETED - {}", transactionSAVED);
        return new ResponseDTO<>(true, "WITHDRAW COMPLETED");
    }

    public ResponseDTO<List<TransactionDTO>> getTransactionHistory(String token) {
        try {
            TokenDTO tokenDTO = externalResoursesConnectionService.authenticateToken(token);
            if (!tokenDTO.isAuthenticated()) {
                return new ResponseDTO<>(false, "token expired");
            }
            List<Transaction> transactionList = this.transactionRepository.findAllByUserId(Long.valueOf(tokenDTO.getUserId()));
            List<TransactionDTO> transactionDTOList = transactionList
                    .stream().map(transactionMapper::toDto).toList();
            return new ResponseDTO<>(true, transactionDTOList);
        } catch (Exception e) {
            log.error("ERROR - {}", e.getMessage());
            return new ResponseDTO<>(false, e.getMessage());
        }
    }

}
