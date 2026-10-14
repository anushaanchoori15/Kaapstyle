package com.kaapstyle.outfit.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "item_wear_history")
public class ItemWearHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "outfit_id")
    private Long outfitId;

    @Column(name = "worn_date", nullable = false)
    private LocalDate wornDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ItemWearHistory() {}

    public ItemWearHistory(Long userId, Long itemId, Long outfitId, LocalDate wornDate) {
        this.userId = userId;
        this.itemId = itemId;
        this.outfitId = outfitId;
        this.wornDate = wornDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public Long getOutfitId() { return outfitId; }
    public void setOutfitId(Long outfitId) { this.outfitId = outfitId; }

    public LocalDate getWornDate() { return wornDate; }
    public void setWornDate(LocalDate wornDate) { this.wornDate = wornDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
