package com.virtualWallet.AuthenticatorMs.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Base64;

@Configuration
public class WebClientConfig {

    @Value("${microservices.users.base-url}")
    private String userMSBaseUrl;

    @Value("${microservices.users.credentials}")
    private String userMSCredentials;

    @Bean
    @LoadBalanced
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean("userWebClientBuilder")
    @LoadBalanced // Eureka resolved Uri
    public WebClient.Builder userWebClientBuilder() {
        String basicAuth = Base64.getEncoder()
                .encodeToString(userMSCredentials.getBytes());
        return WebClient.builder()
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Basic " + basicAuth)
                .baseUrl(userMSBaseUrl);
    }

}
