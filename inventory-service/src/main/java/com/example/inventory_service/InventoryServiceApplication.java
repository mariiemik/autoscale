package com.example.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

//@EnableCaching
//@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.inventory_service",  // 👈 Обязательно добавьте пакет вашего приложения
        "com.example.common.config"})
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }

}
