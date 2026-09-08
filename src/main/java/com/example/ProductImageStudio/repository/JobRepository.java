package com.example.ProductImageStudio.repository;

import com.example.ProductImageStudio.entity.Job;
import com.example.ProductImageStudio.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    Optional<Job> findByJobId(String jobId);

    /** History — only rows that have an image URL, newest first. */
    Page<Job> findByUserAndImageUrlIsNotNullOrderByCreatedAtDesc(
            User user, Pageable pageable);

    /**
     * Atomically flip charged=true iff it was still false. Returns 1 if this
     * call was the one that flipped it, 0 if someone already did. Safe under
     * concurrent status-poll bursts.
     */
    @Modifying
    @Query("UPDATE Job j SET j.charged = true, j.chargedAt = :now " +
           "WHERE j.jobId = :jobId AND j.charged = false")
    int markChargedIfNot(@Param("jobId") String jobId, @Param("now") OffsetDateTime now);
}
