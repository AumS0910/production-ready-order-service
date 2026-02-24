package com.example.orderservice;

import com.example.orderservice.dto.OrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrderSuccessfully() throws Exception {

        OrderRequest request = new OrderRequest("ord-int-1", "Pen", 5);

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "password");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(
                objectMapper.writeValueAsString(request),
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/orders",
                entity,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    void shouldReturn401IfNotAuthenticated() {

        OrderRequest request = new OrderRequest("ord-int-2", "Pen", 5);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/orders",
                request,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void shouldReturn400WhenQuantityIsZero() {

        String url = "http://localhost:" + port + "/orders";

        String badJson = """
            {
                "orderId": "ord-bad-1",
                "itemName": "Pen",
                "quantity": 0
            }
            """;

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("user", "password");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(badJson, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }


}
