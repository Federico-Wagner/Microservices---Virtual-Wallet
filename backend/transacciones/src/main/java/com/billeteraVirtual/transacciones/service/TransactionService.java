package com.billeteraVirtual.transacciones.service;


import com.billeteraVirtual.transacciones.dto.ResponseDTO;
import com.billeteraVirtual.transacciones.dto.TransactionDTO;
import com.billeteraVirtual.transacciones.dto.accountMS.WithdrawRequestDTO;
import com.billeteraVirtual.transacciones.dto.accountMS.WithdrawResponseDTO;
import com.billeteraVirtual.transacciones.dto.authMS.TokenResponseDTO;
import com.billeteraVirtual.transacciones.entity.Transaction;
import com.billeteraVirtual.transacciones.enumerators.TransactionState;
import com.billeteraVirtual.transacciones.repository.TransactionRepository;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {


    private final ExternalResoursesConnectionService externalResoursesConnectionService;

    private final TransactionRepository transactionRepository;

    public TransactionService(ExternalResoursesConnectionService externalResoursesConnectionService,
                              TransactionRepository transactionRepository) {
        this.externalResoursesConnectionService = externalResoursesConnectionService;
        this.transactionRepository = transactionRepository;
    }


    public ResponseDTO<?> transfer(TransactionDTO transactionDTO) {
        // validate token
        TokenResponseDTO tokenResponseDTO = externalResoursesConnectionService.validateTokenForAction(transactionDTO.getToken(), "TRANSACTION");
        if (!tokenResponseDTO.isAuthenticated()) {
            return new ResponseDTO<>(false, "token not authenticated", null);
        }
        // Register Transaction
        Transaction transaction = new Transaction();
        transaction.setAccountIdFrom(transactionDTO.getAccountFrom());
        transaction.setAccountIdTo(transactionDTO.getAccountTo());
        transaction.setAmount(transactionDTO.getAmount());
        transaction.setTransactionState(TransactionState.PENDING);
        Transaction transactionSAVED = this.transactionRepository.save(transaction);
        // Execute Withdraw
        WithdrawRequestDTO withdrawRequestDTO = new WithdrawRequestDTO(transactionDTO);
        WithdrawResponseDTO withdrawResponseDTO = this.externalResoursesConnectionService.executeWithdraw(withdrawRequestDTO);

        // Cambio estado tranferencia
        if (!withdrawResponseDTO.isSuccess()) {
            transactionSAVED.setTransactionState(TransactionState.REJECTED);
            this.transactionRepository.save(transactionSAVED);
            return new ResponseDTO<>(false, "ERROR: " + withdrawResponseDTO.getErrMsg(), null);
        }
        transactionSAVED.setTransactionState(TransactionState.DONE);
        transactionSAVED = this.transactionRepository.save(transactionSAVED);

        return new ResponseDTO<>(true, "WITHDRAW COMPLETED", null);
    }

}
