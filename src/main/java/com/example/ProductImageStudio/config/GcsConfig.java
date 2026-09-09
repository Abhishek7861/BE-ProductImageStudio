package com.example.ProductImageStudio.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

@Configuration
public class GcsConfig {

//    @Value("${gcp.storage.credentials-b64:}")
//    private String credentialsB64;
//
//    @Value("${gcp.storage.project-id:}")
//    private String projectId;
//
//    @Bean
//    public Storage storage() throws IOException {
//        StorageOptions.Builder builder = StorageOptions.newBuilder();
//
//        if (!projectId.isBlank()) {
//            builder.setProjectId(projectId);
//        }
//
//        if (!credentialsB64.isBlank()) {
//            // Env-var mode: decode the JSON key from GCP_SA_KEY_B64
//            byte[] json = Base64.getDecoder().decode(credentialsB64);
//            GoogleCredentials creds = ServiceAccountCredentials
//                    .fromStream(new ByteArrayInputStream(json));
//            builder.setCredentials(creds);
//        }
//        // else: fall back to Application Default Credentials
//        //   (GOOGLE_APPLICATION_CREDENTIALS file, gcloud login, or metadata server)
//
//        return builder.build().getService();
//    }

}
