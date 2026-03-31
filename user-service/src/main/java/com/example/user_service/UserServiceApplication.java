package com.example.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@EnableCaching
//@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = {"com.example.user_service",
        "com.example.common.config"})
//		(exclude = {org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class,
//		org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class})
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
