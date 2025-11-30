package com.example.servicea.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.WebFilter;

@Configuration
public class LoggingConfig {

    @Bean
    public WebFilter loggingFilter() {
        return (exchange, chain) -> {
            long startTime = System.currentTimeMillis();
            
            System.out.println("Исходящий запрос: " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI());
            
            return chain.filter(exchange)
                    .doOnSuccess(aVoid -> {
                        long duration = System.currentTimeMillis() - startTime;
                        System.out.println("Входящий ответ: " + exchange.getResponse().getStatusCode() + 
                                         " для " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI() +
                                         " (время выполнения: " + duration + " мс)");
                    })
                    .doOnError(throwable -> {
                        long duration = System.currentTimeMillis() - startTime;
                        System.err.println("Ошибка в ответе: " + exchange.getRequest().getMethod() + " " + exchange.getRequest().getURI() +
                                         " (время выполнения: " + duration + " мс) - " + throwable.getMessage());
                    });
        };
    }
}