package com.example.payment_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    @LoadBalanced // <-- КЛЮЧЕВАЯ АННОТАЦИЯ
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://inventory-service").build(); // <-- Используем имя сервиса
    }

    @Bean
    public WebClient paymentWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://external-payment-gateway").build(); // <-- Используем имя сервиса
    }
}
