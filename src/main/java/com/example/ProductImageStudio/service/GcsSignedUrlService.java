package com.example.ProductImageStudio.service;

import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ImpersonatedCredentials;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.List;
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

    @Value("${gcp.storage.signer-sa-email:}")
    private String signerSaEmail;

    private ServiceAccountSigner signer;

    @PostConstruct
    void initSigner() throws IOException {
        GoogleCredentials source = GoogleCredentials.getApplicationDefault();
        if (source instanceof ServiceAccountSigner s) {
            signer = s;
        } else if (signerSaEmail != null && !signerSaEmail.isBlank()) {
            signer = ImpersonatedCredentials.create(
                    source,
                    signerSaEmail,
                    null,
                    List.of("https://www.googleapis.com/auth/cloud-platform"),
                    3600);
        } else {
            throw new IllegalStateException(
                    "No signing credentials available. Set gcp.storage.signer-sa-email "
                            + "or run with a service account JSON key.");
        }
    }

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
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.signWith(signer)
        );

        URL readUrl = storage.signUrl(
                BlobInfo.newBuilder(bucketName, objectName).build(),
                READ_URL_HOURS, TimeUnit.HOURS,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature(),
                Storage.SignUrlOption.signWith(signer)
        );

        return new SignedUrls(uploadUrl.toString(), readUrl.toString());
    }

    public record SignedUrls(String uploadUrl, String readUrl) {}
}
