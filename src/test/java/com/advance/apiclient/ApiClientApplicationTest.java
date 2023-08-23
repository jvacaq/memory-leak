package com.advance.apiclient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiClientApplicationTest {

    @LocalServerPort
    private Integer port;

    @Test
    void getResponse() {
        WebTestClient client = WebTestClient.bindToServer()
                .responseTimeout(Duration.ofMinutes(5)).baseUrl("http://localhost:" + port)
                .build();

        Map<String, String> responseBody = client.get()
                .uri("/v1/client")
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(new ParameterizedTypeReference<Map<String, String>>() {
                }).returnResult()
                .getResponseBody();

        assertNotNull(responseBody);
    }
}