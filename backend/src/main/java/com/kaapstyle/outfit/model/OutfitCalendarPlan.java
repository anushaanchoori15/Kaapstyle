package com.kaapstyle.outfit.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "outfit_calendar_plans", uniqueConstraints = {
    @UniqueConstraint(name = "uq_user_date_slot", columnNames = {"user_id", "plan_date", "meal_or_time_slot"})
})
public class OutfitCalendarPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "outfit_id", nullable = false)
    private Long outfitId;

    @Column(name = "plan_date", nullable = false)
    private LocalDate planDate;

    @Column(name = "meal_or_time_slot", nullable = false, length = 30)
    private String mealOrTimeSlot = "DAY";

    @Column(name = "is_worn", nullable = false)
    private Boolean isWorn = false;

    @Column(name = "weather_forecast_summary", length = 100)
    private String weatherForecastSummary;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OutfitCalendarPlan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getOutfitId() { return outfitId; }
    public void setOutfitId(Long outfitId) { this.outfitId = outfitId; }

    public LocalDate getPlanDate() { return planDate; }
    public void setPlanDate(LocalDate planDate) { this.planDate = planDate; }

    public String getMealOrTimeSlot() { return mealOrTimeSlot; }
    public void setMealOrTimeSlot(String mealOrTimeSlot) { this.mealOrTimeSlot = mealOrTimeSlot; }

    public Boolean getIsWorn() { return isWorn; }
    public void setIsWorn(Boolean isWorn) { this.isWorn = isWorn; }

    public String getWeatherForecastSummary() { return weatherForecastSummary; }
    public void setWeatherForecastSummary(String weatherForecastSummary) { this.weatherForecastSummary = weatherForecastSummary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
