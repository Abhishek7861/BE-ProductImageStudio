package com.example.ProductImageStudio.controller;

import com.example.ProductImageStudio.dto.PodImageResponse;
import com.example.ProductImageStudio.dto.RunPodRequest;
import com.example.ProductImageStudio.dto.RunPodResponse;
import com.example.ProductImageStudio.entity.RunPodOutput;
import com.example.ProductImageStudio.entity.RunPodStatusResponse;
import com.example.ProductImageStudio.service.RunPodService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5174")
public class RunPodTestController {

    private final RunPodService runPodService;

    @PostMapping("/generate")
    public RunPodResponse testGenerate(@RequestBody RunPodRequest runPodInput) {
        return runPodService.generateImage(runPodInput);
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
