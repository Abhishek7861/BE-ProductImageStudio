package com.example.ProductImageStudio.dto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

/**
 * Compact page envelope for list endpoints — a friendlier shape than Spring
 * Data's default `Page` JSON (which leaks internal Pageable fields).
 */
public record PageResponse<T>(
        List<T> items,
        long totalCount,
        int page,
        int pageSize,
        int totalPages,
        boolean hasMore
) {
    public static <T, S> PageResponse<T> from(Page<S> source, Function<S, T> mapper) {
        return new PageResponse<>(
                source.getContent().stream().map(mapper).toList(),
                source.getTotalElements(),
                source.getNumber(),
                source.getSize(),
                source.getTotalPages(),
                source.hasNext()
        );
    }
}
