package com.kaapstyle.travel.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "luggage_containers")
public class LuggageContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_id", nullable = false)
    private Long tripId;

    @Column(name = "container_name", nullable = false, length = 100)
    private String containerName;

    @Column(name = "container_type", nullable = false, length = 50)
    private String containerType = "CHECK_IN";

    @Column(name = "max_weight_limit_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal maxWeightLimitKg = new BigDecimal("23.00");

    @Column(name = "current_weight_kg", nullable = false, precision = 5, scale = 2)
    private BigDecimal currentWeightKg = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public LuggageContainer() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getTripId() { return tripId; }
    public void setTripId(Long tripId) { this.tripId = tripId; }

    public String getContainerName() { return containerName; }
    public void setContainerName(String containerName) { this.containerName = containerName; }

    public String getContainerType() { return containerType; }
    public void setContainerType(String containerType) { this.containerType = containerType; }

    public BigDecimal getMaxWeightLimitKg() { return maxWeightLimitKg; }
    public void setMaxWeightLimitKg(BigDecimal maxWeightLimitKg) { this.maxWeightLimitKg = maxWeightLimitKg; }

    public BigDecimal getCurrentWeightKg() { return currentWeightKg; }
    public void setCurrentWeightKg(BigDecimal currentWeightKg) { this.currentWeightKg = currentWeightKg; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
