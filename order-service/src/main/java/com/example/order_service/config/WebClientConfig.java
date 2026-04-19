package com.example.order_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient inventoryWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://inventory-service:8084").build();
//        return builder.baseUrl("http://inventory-service").build();
    }

    @Bean
    public WebClient userWebClient(WebClient.Builder builder) {
//        return builder.baseUrl("http://user-service").build();
        return builder.baseUrl("http://user-service:8088").build();
    }

    @Bean
    public WebClient paymentWebClient(WebClient.Builder builder) {
//        return builder.baseUrl("http://user-service").build();
        return builder.baseUrl("http://payment-service:8082").build();
    }
}
