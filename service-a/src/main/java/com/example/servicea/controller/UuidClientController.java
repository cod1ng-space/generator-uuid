package com.example.servicea.controller;

import com.example.servicea.service.UuidClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UuidClientController {

    private final UuidClientService uuidClientService;

    @Autowired
    public UuidClientController(UuidClientService uuidClientService) {
        this.uuidClientService = uuidClientService;
    }

    @GetMapping("/generate-uuid")
    public Mono<String> generateUuid() {
        return uuidClientService.fetchUuid();
    }
}