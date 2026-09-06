package com.example.ProductImageStudio.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RunPodOutput {

    /** Image URL (or base64 string when enable_base64_output=true). */
    private String result;

    /** RunPod-reported cost of this job in USD. */
    private double cost;
}
