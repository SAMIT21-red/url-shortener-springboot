package com.example.urlshortner.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShortUrlRequest {

    @NotBlank(message = "URL cannot be empty")
    @URL(message = "Invalid URL format")
    private String url;
}
