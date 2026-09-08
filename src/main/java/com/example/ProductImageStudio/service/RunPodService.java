package com.example.ProductImageStudio.service;

import com.example.ProductImageStudio.dto.RunPodRequest;
import com.example.ProductImageStudio.dto.RunPodResponse;
import com.example.ProductImageStudio.entity.Job;
import com.example.ProductImageStudio.entity.RunPodStatusResponse;
import com.example.ProductImageStudio.entity.User;
import com.example.ProductImageStudio.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RunPodService {

    private static final BigDecimal COST_PER_IMAGE = new BigDecimal("20");
    private static final String STATUS_COMPLETED = "COMPLETED";

    private final WebClient webClient;
    private final UserService userService;
    private final JobRepository jobRepository;

    @Transactional
    public RunPodResponse generateImage(OAuth2User oAuth2User, RunPodRequest request) {

        User user = userService.findOrCreate(oAuth2User);

        // 1. Balance guard — fail fast before touching RunPod.
        if (user.getBalance().compareTo(COST_PER_IMAGE) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.PAYMENT_REQUIRED,
                    "Insufficient balance. Need " + COST_PER_IMAGE
                            + ", have " + user.getBalance()
            );
        }

        // 2. Submit to RunPod.
        Map<String, Object> body = Map.of("input", request);
        RunPodResponse response = webClient
                .post()
                .uri("/run")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(RunPodResponse.class)
                .block();

        // 3. Record the job so we can charge exactly once on completion.
        jobRepository.save(Job.builder()
                .jobId(response.getId())
                .user(user)
                .status(response.getStatus())
                .build());

        return response;
    }

    @Transactional
    public RunPodStatusResponse getJobStatus(String jobId) {

        RunPodStatusResponse status = webClient
                .get()
                .uri("/status/" + jobId)
                .retrieve()
                .bodyToMono(RunPodStatusResponse.class)
                .block();

        // Charge on the first COMPLETED we see for this job.
        if (STATUS_COMPLETED.equalsIgnoreCase(status.getStatus())) {
            int flipped = jobRepository.markChargedIfNot(jobId, OffsetDateTime.now());
            if (flipped == 1) {
                jobRepository.findByJobId(jobId).ifPresent(job ->
                        userService.debit(job.getUser(), COST_PER_IMAGE)
                );
            }
        }

        // Keep the local job row's status + image URL in sync with the last poll.
        jobRepository.findByJobId(jobId).ifPresent(job -> {
            job.setStatus(status.getStatus());
            if (status.getOutput() != null && status.getOutput().getResult() != null) {
                job.setImageUrl(status.getOutput().getResult());
            }
        });

        return status;
    }
}
