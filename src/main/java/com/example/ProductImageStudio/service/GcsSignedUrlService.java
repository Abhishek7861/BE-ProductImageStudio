package com.example.ProductImageStudio.service;

import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GcsSignedUrlService {

    private static final int UPLOAD_URL_MINUTES = 15;
    private static final int READ_URL_HOURS = 1;

    private final Storage storage;

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    public SignedUrls generateUploadUrl(String originalFileName, String contentType) {
        if (originalFileName == null) {
            throw new IllegalArgumentException("originalFileName must not be null");
        }

        String extension = "";
        int dotIndex = originalFileName.lastIndexOf(".");
        if (dotIndex > 0) {
            extension = originalFileName.substring(dotIndex);
        }

        String objectName = "images/" + UUID.randomUUID() + extension;

        BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, objectName)
                .setContentType(contentType)
                .build();

        URL uploadUrl = storage.signUrl(
                blobInfo,
                UPLOAD_URL_MINUTES, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(Map.of("Content-Type", contentType)),
                Storage.SignUrlOption.withV4Signature()
        );

        URL readUrl = storage.signUrl(
                BlobInfo.newBuilder(bucketName, objectName).build(),
                READ_URL_HOURS, TimeUnit.HOURS,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature()
        );

        return new SignedUrls(uploadUrl.toString(), readUrl.toString());
    }

    public record SignedUrls(String uploadUrl, String readUrl) {}
}
