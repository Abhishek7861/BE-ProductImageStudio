package com.example.ProductImageStudio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RunPodRequest {

    private List<String> images;
    private boolean enable_base64_output = false;
    private boolean enable_sync_mode = false;
    private String output_format = "jpeg";
    private String prompt;
    private long seed = -1;
    private String size = "1024*1024";
}
