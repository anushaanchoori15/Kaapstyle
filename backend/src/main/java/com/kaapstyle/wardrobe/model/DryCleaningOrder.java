package com.kaapstyle.wardrobe.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "dry_cleaning_orders")
public class DryCleaningOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "service_provider_name", nullable = false, length = 150)
    private String serviceProviderName;

    @Column(name = "dropoff_date", nullable = false)
    private LocalDate dropoffDate;

    @Column(name = "expected_pickup_date")
    private LocalDate expectedPickupDate;

    @Column(name = "actual_pickup_date")
    private LocalDate actualPickupDate;

    @Column(name = "total_cost", precision = 8, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "order_status", nullable = false, length = 30)
    private String orderStatus = "DROPPED_OFF"; // DROPPED_OFF, READY_FOR_PICKUP, COMPLETED, CANCELLED

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DryCleaningOrder() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getServiceProviderName() { return serviceProviderName; }
    public void setServiceProviderName(String serviceProviderName) { this.serviceProviderName = serviceProviderName; }

    public LocalDate getDropoffDate() { return dropoffDate; }
    public void setDropoffDate(LocalDate dropoffDate) { this.dropoffDate = dropoffDate; }

    public LocalDate getExpectedPickupDate() { return expectedPickupDate; }
    public void setExpectedPickupDate(LocalDate expectedPickupDate) { this.expectedPickupDate = expectedPickupDate; }

    public LocalDate getActualPickupDate() { return actualPickupDate; }
    public void setActualPickupDate(LocalDate actualPickupDate) { this.actualPickupDate = actualPickupDate; }

    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }

    public String getOrderStatus() { return orderStatus; }
    public void setOrderStatus(String orderStatus) { this.orderStatus = orderStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
