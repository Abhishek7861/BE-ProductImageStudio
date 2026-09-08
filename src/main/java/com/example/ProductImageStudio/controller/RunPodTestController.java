package com.example.ProductImageStudio.controller;

import com.example.ProductImageStudio.dto.PodImageResponse;
import com.example.ProductImageStudio.dto.RunPodRequest;
import com.example.ProductImageStudio.dto.RunPodResponse;
import com.example.ProductImageStudio.entity.RunPodOutput;
import com.example.ProductImageStudio.entity.RunPodStatusResponse;
import com.example.ProductImageStudio.service.RunPodService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RunPodTestController {

    private final RunPodService runPodService;

    @PostMapping("/generate")
    public RunPodResponse testGenerate(@AuthenticationPrincipal OAuth2User oauthUser,
                                       @RequestBody RunPodRequest runPodInput) {
        return runPodService.generateImage(oauthUser, runPodInput);
    }

    @GetMapping("/status/{jobId}")
    public PodImageResponse getStatus(@PathVariable String jobId) {

        RunPodStatusResponse status = runPodService.getJobStatus(jobId);

        PodImageResponse resp = new PodImageResponse();
        resp.setId(status.getId());
        resp.setStatus(status.getStatus());
        RunPodOutput output = status.getOutput();
        if (output != null) {
            resp.setImage_url(output.getResult());
        }
        return resp;
    }


}
