package com.example.urlshortner.service;

import com.example.urlshortner.entity.UrlMapping;
import com.example.urlshortner.exception.UrlNotFoundException;
import com.example.urlshortner.repository.UrlMappingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UrlMappingService {

    private static final String CHAR_SET =
            "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int SHORT_CODE_LENGTH = 6;

    private final UrlMappingRepository repository;
    private final SecureRandom random = new SecureRandom();

    /**
     * Create or return existing short URL
     */
    public String createShortUrl(String originalUrl) {

        // Optional: avoid duplicate short URLs for same long URL
        return repository.findByOriginalUrl(originalUrl)
                .map(UrlMapping::getShortCode)
                .orElseGet(() -> generateAndSave(originalUrl));
    }

    /**
     * Resolve short code → original URL
     */
    public String getOriginalUrl(String shortCode) {
        UrlMapping mapping = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("Short URL not found"));


        // Increase click count
        mapping.setClickCount(mapping.getClickCount() + 1);
        repository.save(mapping);

        return mapping.getOriginalUrl();
    }

    /**
     * Generate unique short code and save
     */
    private String generateAndSave(String originalUrl) {
        String shortCode;

        do {
            shortCode = generateShortCode();
        } while (repository.existsByShortCode(shortCode));

        UrlMapping mapping = UrlMapping.builder()
                .originalUrl(originalUrl)
                .shortCode(shortCode)
                .createdAt(LocalDateTime.now())
                .clickCount(0L)
                .build();

        repository.save(mapping);
        return shortCode;
    }

    /**
     * Random short code generator
     */
    private String generateShortCode() {
        StringBuilder sb = new StringBuilder(SHORT_CODE_LENGTH);
        for (int i = 0; i < SHORT_CODE_LENGTH; i++) {
            sb.append(CHAR_SET.charAt(random.nextInt(CHAR_SET.length())));
        }
        return sb.toString();
    }
}
