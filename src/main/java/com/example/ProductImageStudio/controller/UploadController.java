package com.example.ProductImageStudio.controller;

import com.example.ProductImageStudio.dto.SignedUrlRequest;
import com.example.ProductImageStudio.service.GcsSignedUrlService;
import com.example.ProductImageStudio.service.GcsSignedUrlService.SignedUrls;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/uploads")
@CrossOrigin(origins = "http://localhost:5174")
@RequiredArgsConstructor
public class UploadController {

    private final GcsSignedUrlService signedUrlService;

    @PostMapping("/signed-url")
    public SignedUrls generateSignedUrl(@RequestBody SignedUrlRequest request) {
        return signedUrlService.generateUploadUrl(
                request.fileName(),
                request.contentType()
        );
    }
}
