package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableCaching
@EnableRetry
@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@EnableKafka
public class OrderserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderserviceApplication.class, args);
	}

}
