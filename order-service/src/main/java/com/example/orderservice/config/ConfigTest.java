package com.example.orderservice.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class ConfigTest {

    @Value("${custom.message}")
    private String message;

    @PostConstruct
    public void printMessage() {
        System.out.println("Config Server Message:" + message);
    }
}
