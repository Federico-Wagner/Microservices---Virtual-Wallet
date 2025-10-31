package com.BilleteraVirtual.accounts.service;

import com.BilleteraVirtual.accounts.dto.TokenDTO;
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
