package com.advance.apiclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.Map;

@SpringBootApplication
@RestController
@RequestMapping("/v1/client")
public class ApiClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiClientApplication.class, args);
	}

	@GetMapping
	public ResponseEntity<Map<String, String>> getResponse() throws IOException {
		ClassPathResource cp = new ClassPathResource("img.png");

		String payload = new String (Base64.getEncoder().encode(cp.getInputStream().readAllBytes()));
		Map<String, String> body = Map.of("body", payload);
		HttpClient httpClient = HttpClient.create().responseTimeout(Duration.ofSeconds(1));
		WebClient client = WebClient.builder()
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.baseUrl("http://localhost:8080")
				.build();

		Map<String, String> block = client.post()
				.uri("http://localhost:8080/v1/long-run/do-response")
				.body(Mono.just(body), new ParameterizedTypeReference<Map<String, String>>() {
				}).exchangeToMono(clientResponse -> {
					if (clientResponse.statusCode().isError()) {
						throw new ResponseStatusException(HttpStatus.BAD_GATEWAY);
					} else {
						clientResponse.body((inputMessage, context) -> inputMessage.getBody());

						return clientResponse.bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {
						});
					}
				}).block();

		return ResponseEntity.ok(block);

	}

}
