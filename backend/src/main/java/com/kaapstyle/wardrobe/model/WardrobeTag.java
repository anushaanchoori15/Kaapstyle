package com.kaapstyle.wardrobe.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "wardrobe_tags")
public class WardrobeTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tag_name", nullable = false, unique = true, length = 50)
    private String tagName;

    @Column(name = "tag_type", nullable = false, length = 30)
    private String tagType = "CUSTOM";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public WardrobeTag() {}

    public WardrobeTag(String tagName, String tagType) {
        this.tagName = tagName;
        this.tagType = tagType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTagName() { return tagName; }
    public void setTagName(String tagName) { this.tagName = tagName; }

    public String getTagType() { return tagType; }
    public void setTagType(String tagType) { this.tagType = tagType; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
