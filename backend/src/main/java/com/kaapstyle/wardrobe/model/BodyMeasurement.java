package com.kaapstyle.wardrobe.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "body_measurements")
public class BodyMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "profile_tag", nullable = false, length = 50)
    private String profileTag = "SELF";

    @Column(name = "chest_cm", precision = 5, scale = 2)
    private BigDecimal chestCm;

    @Column(name = "waist_cm", precision = 5, scale = 2)
    private BigDecimal waistCm;

    @Column(name = "hips_cm", precision = 5, scale = 2)
    private BigDecimal hipsCm;

    @Column(name = "inseam_cm", precision = 5, scale = 2)
    private BigDecimal inseamCm;

    @Column(name = "shoulder_width_cm", precision = 5, scale = 2)
    private BigDecimal shoulderWidthCm;

    @Column(name = "recorded_at", nullable = false)
    private LocalDate recordedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BodyMeasurement() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getProfileTag() { return profileTag; }
    public void setProfileTag(String profileTag) { this.profileTag = profileTag; }

    public BigDecimal getChestCm() { return chestCm; }
    public void setChestCm(BigDecimal chestCm) { this.chestCm = chestCm; }

    public BigDecimal getWaistCm() { return waistCm; }
    public void setWaistCm(BigDecimal waistCm) { this.waistCm = waistCm; }

    public BigDecimal getHipsCm() { return hipsCm; }
    public void setHipsCm(BigDecimal hipsCm) { this.hipsCm = hipsCm; }

    public BigDecimal getInseamCm() { return inseamCm; }
    public void setInseamCm(BigDecimal inseamCm) { this.inseamCm = inseamCm; }

    public BigDecimal getShoulderWidthCm() { return shoulderWidthCm; }
    public void setShoulderWidthCm(BigDecimal shoulderWidthCm) { this.shoulderWidthCm = shoulderWidthCm; }

    public LocalDate getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDate recordedAt) { this.recordedAt = recordedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
