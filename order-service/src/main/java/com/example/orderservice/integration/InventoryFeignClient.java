package com.example.orderservice.integration;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryFeignClient {

    @PostMapping("/inventory/reserve")
    void reserveStock(
            @RequestParam("itemName") String itemName,
            @RequestParam("quantity") int quantity
    );
}
