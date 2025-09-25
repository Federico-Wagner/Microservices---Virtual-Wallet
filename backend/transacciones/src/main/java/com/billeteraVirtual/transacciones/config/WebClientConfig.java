package com.billeteraVirtual.transacciones.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${microservices.auth.base-url}")
    private String authMSBaseUrl;

    @Value("${microservices.accounts.base-url}")
    private String accountsMSBaseUrl;

//    @Value("${microservices.auth.credentials}")
//    private String authMSCredentials;

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }

    @Bean("webClientAuth")
    public WebClient webClientAuth() {
//        String basicAuth = Base64.getEncoder()
//                .encodeToString(authMSCredentials.getBytes());
        return WebClient.builder()
                .baseUrl(authMSBaseUrl)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .build();
    }

    @Bean("webClientAccounts")
    public WebClient webClientAccounts() {
//        String basicAuth = Base64.getEncoder()
//                .encodeToString(authMSCredentials.getBytes());
        return WebClient.builder()
                .baseUrl(accountsMSBaseUrl)
//                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .build();
    }


}