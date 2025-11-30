package com.example.serviceb.controller;

import com.example.serviceb.service.UuidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
public class UuidController {

    @Autowired
    private UuidService uuidService;

    @GetMapping("/uuid")
    public Mono<String> generateUuid() {
        return uuidService.generateUuid();
    }
}