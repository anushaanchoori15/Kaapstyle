package com.kaapstyle.profile.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "measurement_unit")
    private String measurementUnit;

    @Column(name = "theme_mode")
    private String themeMode;

    @Column(name = "is_family_sharing_enabled", nullable = false)
    private boolean isFamilySharingEnabled;

    @Column(name = "is_telemetry_enabled", nullable = false)
    private boolean isTelemetryEnabled;

    @Column(name = "updated_at")
    private Instant updatedAt;

    public UserProfile() {
    }

    @PrePersist
    @PreUpdate
    protected void touchUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getMeasurementUnit() {
        return measurementUnit;
    }

    public void setMeasurementUnit(String measurementUnit) {
        this.measurementUnit = measurementUnit;
    }

    public String getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(String themeMode) {
        this.themeMode = themeMode;
    }

    public boolean isFamilySharingEnabled() {
        return isFamilySharingEnabled;
    }

    public void setFamilySharingEnabled(boolean familySharingEnabled) {
        this.isFamilySharingEnabled = familySharingEnabled;
    }

    public boolean isTelemetryEnabled() {
        return isTelemetryEnabled;
    }

    public void setTelemetryEnabled(boolean telemetryEnabled) {
        this.isTelemetryEnabled = telemetryEnabled;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}