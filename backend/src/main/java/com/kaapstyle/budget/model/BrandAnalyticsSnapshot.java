package com.kaapstyle.budget.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "brand_analytics_snapshots")
public class BrandAnalyticsSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Column(name = "item_count", nullable = false)
    private Integer itemCount = 1;

    @Column(name = "total_spend", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalSpend = BigDecimal.ZERO;

    @Column(name = "avg_cpw", nullable = false, precision = 10, scale = 2)
    private BigDecimal avgCpw = BigDecimal.ZERO;

    @Column(name = "satisfaction_score", nullable = false, precision = 3, scale = 2)
    private BigDecimal satisfactionScore = new BigDecimal("5.00");

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public BrandAnalyticsSnapshot() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public BigDecimal getTotalSpend() { return totalSpend; }
    public void setTotalSpend(BigDecimal totalSpend) { this.totalSpend = totalSpend; }

    public BigDecimal getAvgCpw() { return avgCpw; }
    public void setAvgCpw(BigDecimal avgCpw) { this.avgCpw = avgCpw; }

    public BigDecimal getSatisfactionScore() { return satisfactionScore; }
    public void setSatisfactionScore(BigDecimal satisfactionScore) { this.satisfactionScore = satisfactionScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
