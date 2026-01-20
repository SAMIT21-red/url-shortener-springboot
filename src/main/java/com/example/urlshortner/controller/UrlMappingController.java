package com.example.urlshortner.controller;

import com.example.urlshortner.service.UrlMappingService;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import jakarta.validation.Valid;
import com.example.urlshortner.dto.ShortUrlResponse;
import com.example.urlshortner.dto.ShortUrlRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
public class UrlMappingController {

    private final UrlMappingService service;

    /**
     * Create short URL
     */
    @PostMapping("/shorten")
    public ResponseEntity<ShortUrlResponse> shortenUrl(
            @Valid @RequestBody ShortUrlRequest request) {

        String shortCode = service.createShortUrl(request.getUrl());

        // IMPORTANT: include /r/
        String shortUrl = "http://localhost:8080/r/" + shortCode;

        return ResponseEntity.ok(new ShortUrlResponse(shortUrl));
    }

    /**
     * Redirect short URL → original URL
     */
    @GetMapping("/r/{shortCode}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode) {

        String originalUrl = service.getOriginalUrl(shortCode);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(originalUrl))
                .build();
    }

    // ==========================
    // DTOs
    // ==========================

    @Data
    static class ShortUrlRequest {
        @NotBlank
        private String url;
    }

    @Data
    static class ShortUrlResponse {
        private final String shortUrl;
    }
}
