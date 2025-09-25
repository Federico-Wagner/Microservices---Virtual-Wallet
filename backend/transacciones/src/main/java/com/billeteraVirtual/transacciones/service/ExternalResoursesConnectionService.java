package com.billeteraVirtual.transacciones.service;

import com.billeteraVirtual.transacciones.dto.accountMS.WithdrawRequestDTO;
import com.billeteraVirtual.transacciones.dto.accountMS.WithdrawResponseDTO;
import com.billeteraVirtual.transacciones.dto.authMS.TokenRequestDTO;
import com.billeteraVirtual.transacciones.dto.authMS.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Service
public class ExternalResoursesConnectionService {


    private final WebClient webClient;
    private final @Qualifier("webClientAuth") WebClient webClientAuth;
    private final @Qualifier("webClientAccounts") WebClient webClientAccounts;


    private final String accountMSbaseUrl = "http://localhost:8080";
    private final String userMSbaseUrl = "http://localhost:8090";

    public ExternalResoursesConnectionService(WebClient webClient,
                                              WebClient webClientAuth, WebClient webClientAccounts) {
        this.webClient = webClient;
        this.webClientAuth = webClientAuth;
        this.webClientAccounts = webClientAccounts;
    }

    public TokenResponseDTO validateTokenForAction(String token, String serviceName) {
        TokenRequestDTO tokenRequestDTO = new TokenRequestDTO(token, serviceName);
        return webClientAuth.post()
                .uri( "/validateTokenForAction")
                .bodyValue(tokenRequestDTO)
                .retrieve()
                .bodyToMono(TokenResponseDTO.class)
                .block();
    }

//    public Object consultarCuentasParaTransferencia(Long accountFrom, Long accountTo, BigDecimal amount) {
//        WithdrawRequestDTO withdrawRequestDTO = new WithdrawRequestDTO(accountFrom, accountTo, amount);
//        return webClient.post()
//                .uri(accountMSbaseUrl + "/consultarCuentasParaTransferencia", withdrawRequestDTO)
//                .retrieve()
//                .bodyToMono(Object.class)
//                .block();
//    }

    public WithdrawResponseDTO executeWithdraw(WithdrawRequestDTO withdrawRequestDTO) {
        return webClientAccounts.post()
                .uri("/executeWithdraw")
                .bodyValue(withdrawRequestDTO)
                .retrieve()
                .bodyToMono(WithdrawResponseDTO.class)
                .block();
    }

    public Object validarUsuariosActivos(WithdrawRequestDTO withdrawRequestDTO) {
        return webClient.post()
                .uri(userMSbaseUrl + "/validarUsuariosActivos", withdrawRequestDTO)
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }


}
