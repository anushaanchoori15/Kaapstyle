package com.kaapstyle.wardrobe.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_laundry_statuses")
public class ItemLaundryStatus {

    @Id
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "CLEAN"; // CLEAN, IN_HAMPER, WASHING, DRY_CLEANING

    @Column(name = "wash_cycle_count", nullable = false)
    private Integer washCycleCount = 0;

    @Column(name = "care_instruction_tags", columnDefinition = "json")
    private String careInstructionTags;

    @Column(name = "last_washed_date")
    private LocalDate lastWashedDate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public ItemLaundryStatus() {}

    public ItemLaundryStatus(Long itemId, Long userId) {
        this.itemId = itemId;
        this.userId = userId;
        this.status = "CLEAN";
    }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getWashCycleCount() { return washCycleCount; }
    public void setWashCycleCount(Integer washCycleCount) { this.washCycleCount = washCycleCount; }

    public String getCareInstructionTags() { return careInstructionTags; }
    public void setCareInstructionTags(String careInstructionTags) { this.careInstructionTags = careInstructionTags; }

    public LocalDate getLastWashedDate() { return lastWashedDate; }
    public void setLastWashedDate(LocalDate lastWashedDate) { this.lastWashedDate = lastWashedDate; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
