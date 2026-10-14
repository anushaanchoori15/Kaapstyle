package com.kaapstyle.wardrobe.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "capsule_wardrobes")
public class CapsuleWardrobe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "capsule_name", nullable = false, length = 150)
    private String capsuleName;

    @Column(name = "season", nullable = false, length = 30)
    private String season = "ALL_SEASON";

    @Column(name = "target_item_count", nullable = false)
    private Integer targetItemCount = 30;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public CapsuleWardrobe() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getCapsuleName() { return capsuleName; }
    public void setCapsuleName(String capsuleName) { this.capsuleName = capsuleName; }

    public String getSeason() { return season; }
    public void setSeason(String season) { this.season = season; }

    public Integer getTargetItemCount() { return targetItemCount; }
    public void setTargetItemCount(Integer targetItemCount) { this.targetItemCount = targetItemCount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
