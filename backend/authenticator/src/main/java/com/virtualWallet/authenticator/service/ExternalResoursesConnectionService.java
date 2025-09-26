package com.virtualWallet.authenticator.service;

import com.virtualWallet.authenticator.dto.UserCredentialsRequestDTO;
import com.virtualWallet.authenticator.dto.UserCredentialsResponseDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ExternalResoursesConnectionService {

    @Value("${microservices.users.base-url}")
    private String userMSBaseUrl;

    private final WebClient webClient;
    private final @Qualifier("userWebClientBuilder") WebClient webClientUsers;

    public ExternalResoursesConnectionService(WebClient.Builder webClientBuilder,
                                              WebClient.Builder userWebClientBuilder) {
        this.webClient = webClientBuilder.build();
        this.webClientUsers = userWebClientBuilder.build();
    }

    public UserCredentialsResponseDTO validateUserCredentials(String dni, String password) {
        UserCredentialsRequestDTO requestDTO = new UserCredentialsRequestDTO(dni, password);
        return webClientUsers.post()
                .uri("/validateUserCredentials")
                .bodyValue(requestDTO)
                .retrieve()
                .bodyToMono(UserCredentialsResponseDTO.class)
                .block();
    }

}
