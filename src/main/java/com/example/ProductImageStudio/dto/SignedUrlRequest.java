package com.example.ProductImageStudio.dto;

public record SignedUrlRequest(
        String fileName,
        String contentType
) {
}
