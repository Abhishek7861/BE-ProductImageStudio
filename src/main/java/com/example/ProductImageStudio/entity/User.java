package com.example.ProductImageStudio.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(
        name = "users",
        indexes = @Index(name = "idx_users_email", columnList = "email", unique = true)
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(length = 120)
    private String name;

    @Column(name = "picture_url", columnDefinition = "TEXT")
    private String pictureUrl;

    /** Credits available to spend on generations. */
    @Column(nullable = false, precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal balance = new BigDecimal("100.00");

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
