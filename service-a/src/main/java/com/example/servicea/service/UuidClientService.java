package com.example.servicea.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

@Service
public class UuidClientService {

    private final WebClient webClient;

    @Autowired
    public UuidClientService(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<String> fetchUuid() {
        return webClient.get()
                .uri("/uuid")
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofSeconds(1))
                                .filter(throwable -> throwable instanceof WebClientResponseException)
                )
                .timeout(Duration.ofSeconds(10))
                .onErrorResume(throwable -> {
                    System.err.println("Ошибка при получении UUID: " + throwable.getMessage());
                    return Mono.just("Ошибка генерации UUID");
                });
    }
}