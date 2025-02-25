package com.example.onlineshopping;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
// @SpringBootApplication
public class OnlineshoppingApplication {
    public static void main(String[] args) {
        SpringApplication.run(OnlineshoppingApplication.class, args);
    }
}
