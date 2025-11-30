package com.example.serviceb.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface UuidService {
    Mono<String> generateUuid();
}