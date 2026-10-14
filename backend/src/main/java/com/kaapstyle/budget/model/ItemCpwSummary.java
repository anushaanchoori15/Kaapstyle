package com.kaapstyle.budget.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_cpw_summaries")
public class ItemCpwSummary {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "purchase_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal purchasePrice = BigDecimal.ZERO;

    @Column(name = "wear_count", nullable = false)
    private Integer wearCount = 0;

    @Column(name = "current_cpw", nullable = false, precision = 10, scale = 2)
    private BigDecimal currentCpw = BigDecimal.ZERO;

    @Column(name = "value_score_rating", nullable = false, length = 30)
    private String valueScoreRating = "NEW"; // NEW, GREAT_VALUE, MODERATE_VALUE, UNDERUTILIZED

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ItemCpwSummary() {}

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public BigDecimal getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(BigDecimal purchasePrice) { this.purchasePrice = purchasePrice; }

    public Integer getWearCount() { return wearCount; }
    public void setWearCount(Integer wearCount) { this.wearCount = wearCount; }

    public BigDecimal getCurrentCpw() { return currentCpw; }
    public void setCurrentCpw(BigDecimal currentCpw) { this.currentCpw = currentCpw; }

    public String getValueScoreRating() { return valueScoreRating; }
    public void setValueScoreRating(String valueScoreRating) { this.valueScoreRating = valueScoreRating; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
