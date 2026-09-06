package com.example.ProductImageStudio.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PodImageResponse {
    private String id;
    private String image_url;
    private String status;
}
