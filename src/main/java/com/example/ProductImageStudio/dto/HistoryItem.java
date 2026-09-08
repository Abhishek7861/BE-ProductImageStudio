package com.example.ProductImageStudio.dto;

import com.example.ProductImageStudio.entity.Job;

import java.time.OffsetDateTime;

public record HistoryItem(
        Long id,
        String jobId,
        String imageUrl,
        String status,
        OffsetDateTime createdAt
) {
    public static HistoryItem from(Job job) {
        return new HistoryItem(
                job.getId(),
                job.getJobId(),
                job.getImageUrl(),
                job.getStatus(),
                job.getCreatedAt()
        );
    }
}
