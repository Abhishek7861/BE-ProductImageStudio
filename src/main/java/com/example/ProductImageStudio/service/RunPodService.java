package com.example.ProductImageStudio.service;

import com.example.ProductImageStudio.dto.RunPodRequest;
import com.example.ProductImageStudio.dto.RunPodResponse;
import com.example.ProductImageStudio.entity.RunPodStatusResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@AllArgsConstructor
public class RunPodService {

    private final WebClient webClient;
    private final UserService userService;

    public RunPodResponse generateImage(RunPodRequest request) {

        Map<String, Object> body = Map.of("input", request);

        return webClient
                .post()
                .uri("/run")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(RunPodResponse.class)
                .block();
    }

    public RunPodStatusResponse getJobStatus(String jobId) {

        return webClient
                .get()
                .uri("/status/" + jobId)
                .retrieve()
                .bodyToMono(RunPodStatusResponse.class)
                .block();
    }


}
