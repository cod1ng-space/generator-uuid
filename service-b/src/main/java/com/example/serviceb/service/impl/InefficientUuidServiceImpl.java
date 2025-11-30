package com.example.serviceb.service.impl;

import com.example.serviceb.service.UuidService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class InefficientUuidServiceImpl implements UuidService {

    @Override
    public Mono<String> generateUuid() {
        return Mono.fromCallable(this::generateInefficientUuid);
    }

    private String generateInefficientUuid() {
        // Шаг 1: Создаём SecureRandom на каждый вызов
        SecureRandom secureRandom = new SecureRandom();

        // Шаг 2: Генерируем 10 промежуточных строк
        List<String> intermediates = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            byte[] bytes = new byte[16];
            secureRandom.nextBytes(bytes);
            String raw = Base64.getEncoder().encodeToString(bytes);

            // Шаг 3: Многократное Base64 encode/decode (лишнее)
            for (int j = 0; j < 3; j++) {
                raw = new String(Base64.getDecoder().decode(
                    Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8))
                ), StandardCharsets.UTF_8);
            }
            intermediates.add(raw);
        }

        // Шаг 4: Объединяем всё в одну строку
        StringBuilder combined = new StringBuilder();
        intermediates.forEach(combined::append);

        // Шаг 5: Рефлексия — получаем внутренний массив char из строки
        String combinedStr = combined.toString();
        String reflectedStr;
        try {
            Field valueField = String.class.getDeclaredField("value");
            valueField.setAccessible(true);
            char[] chars = (char[]) valueField.get(combinedStr);
            reflectedStr = new String(chars); // формально не нужно, но "лишняя" операция
        } catch (Exception e) {
            reflectedStr = combinedStr; // fallback без рефлексии на случай ограничений JVM
        }

        // Шаг 6: Используем часть строки для генерации корректного UUID
        // Берём первые 16 байт из отражённой строки (или дополняем нулями)
        byte[] uuidBytes = new byte[16];
        byte[] inputBytes = reflectedStr.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(inputBytes, 0, uuidBytes, 0, Math.min(16, inputBytes.length));

        // Шаг 7: Вручную формируем UUID в правильном формате (version 4)
        long mostSigBits = 0;
        long leastSigBits = 0;
        for (int i = 0; i < 8; i++) {
            mostSigBits = (mostSigBits << 8) | (uuidBytes[i] & 0xFF);
        }
        for (int i = 8; i < 16; i++) {
            leastSigBits = (leastSigBits << 8) | (uuidBytes[i] & 0xFF);
        }

        // Устанавливаем версию (4) и вариант (RFC 4122)
        mostSigBits &= 0xFFFFFFFFFFFF0FFFL; // удаляем версию
        mostSigBits |= 0x0000000000004000L; // устанавливаем версию 4
        leastSigBits &= 0x3FFFFFFFFFFFFFFFL; // удаляем вариант
        leastSigBits |= 0x8000000000000000L; // ставим вариант IETF

        String uuidStr = new UUID(mostSigBits, leastSigBits).toString();

        return uuidStr;
    }
}