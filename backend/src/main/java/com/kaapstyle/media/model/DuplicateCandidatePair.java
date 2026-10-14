package com.kaapstyle.media.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "duplicate_candidate_pairs")
public class DuplicateCandidatePair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "original_media_id", nullable = false)
    private Long originalMediaId;

    @Column(name = "candidate_media_id", nullable = false)
    private Long candidateMediaId;

    @Column(name = "similarity_score", nullable = false, precision = 5, scale = 4)
    private BigDecimal similarityScore;

    @Column(name = "resolution_status", nullable = false, length = 20)
    private String resolutionStatus = "PENDING"; // PENDING, KEPT_BOTH, DELETED_CANDIDATE, MERGED

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DuplicateCandidatePair() {}

    public DuplicateCandidatePair(Long userId, Long originalMediaId, Long candidateMediaId, BigDecimal similarityScore) {
        this.userId = userId;
        this.originalMediaId = originalMediaId;
        this.candidateMediaId = candidateMediaId;
        this.similarityScore = similarityScore;
        this.resolutionStatus = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOriginalMediaId() { return originalMediaId; }
    public void setOriginalMediaId(Long originalMediaId) { this.originalMediaId = originalMediaId; }

    public Long getCandidateMediaId() { return candidateMediaId; }
    public void setCandidateMediaId(Long candidateMediaId) { this.candidateMediaId = candidateMediaId; }

    public BigDecimal getSimilarityScore() { return similarityScore; }
    public void setSimilarityScore(BigDecimal similarityScore) { this.similarityScore = similarityScore; }

    public String getResolutionStatus() { return resolutionStatus; }
    public void setResolutionStatus(String resolutionStatus) { this.resolutionStatus = resolutionStatus; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
