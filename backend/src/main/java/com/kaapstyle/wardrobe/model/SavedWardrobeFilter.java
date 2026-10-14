package com.kaapstyle.wardrobe.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "saved_wardrobe_filters")
public class SavedWardrobeFilter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "filter_name", nullable = false, length = 100)
    private String filterName;

    @Column(name = "category_ids", columnDefinition = "json")
    private String categoryIds;

    @Column(name = "color_hex_list", columnDefinition = "json")
    private String colorHexList;

    @Column(name = "seasons", columnDefinition = "json")
    private String seasons;

    @Column(name = "tags", columnDefinition = "json")
    private String tags;

    @Column(name = "sort_by", nullable = false, length = 50)
    private String sortBy = "CREATED_DESC";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public SavedWardrobeFilter() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getFilterName() { return filterName; }
    public void setFilterName(String filterName) { this.filterName = filterName; }

    public String getCategoryIds() { return categoryIds; }
    public void setCategoryIds(String categoryIds) { this.categoryIds = categoryIds; }

    public String getColorHexList() { return colorHexList; }
    public void setColorHexList(String colorHexList) { this.colorHexList = colorHexList; }

    public String getSeasons() { return seasons; }
    public void setSeasons(String seasons) { this.seasons = seasons; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
