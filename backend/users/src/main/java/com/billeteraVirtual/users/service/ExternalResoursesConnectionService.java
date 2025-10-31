package com.billeteraVirtual.users.service;

import com.billeteraVirtual.users.dto.ResponseDTO;
import com.billeteraVirtual.users.dto.TokenDTO;
import com.billeteraVirtual.users.dto.TokenRequestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
public class ExternalResoursesConnectionService {

    @Value("${microservices.auth.base-url}")
    private String authMSBaseUrl;

    private final WebClient webClient;

    public ExternalResoursesConnectionService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();

    }

    public ResponseDTO<?> generateToken(String id, String cuit, String role) {
        String url = authMSBaseUrl + "/auth/generateToken";
        TokenRequestDTO requestDTO = new TokenRequestDTO(id, cuit, role);
        return this.executePost(url, requestDTO, ResponseDTO.class);
    }

    public TokenDTO authenticateToken(String token) {
        String url = authMSBaseUrl + "/auth/authenticateToken";
        return this.executePost(url, token, TokenDTO.class);
    }


    public <T, V> T executePost(String url, V body, Class<T> responseType) {
        try {
            return webClient.post()
                    .uri(url)
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("Error POST - url: {} : {} - {}", url, e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

}
