package com.kaapstyle.outfit.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "color_rules")
public class ColorRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType;

    @Column(name = "base_color_hex", nullable = false, length = 10)
    private String baseColorHex;

    @Column(name = "matching_colors_hex", nullable = false, columnDefinition = "json")
    private String matchingColorsHex;

    @Column(name = "harmony_score", nullable = false, precision = 3, scale = 2)
    private BigDecimal harmonyScore = BigDecimal.ONE;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ColorRule() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getRuleType() { return ruleType; }
    public void setRuleType(String ruleType) { this.ruleType = ruleType; }

    public String getBaseColorHex() { return baseColorHex; }
    public void setBaseColorHex(String baseColorHex) { this.baseColorHex = baseColorHex; }

    public String getMatchingColorsHex() { return matchingColorsHex; }
    public void setMatchingColorsHex(String matchingColorsHex) { this.matchingColorsHex = matchingColorsHex; }

    public BigDecimal getHarmonyScore() { return harmonyScore; }
    public void setHarmonyScore(BigDecimal harmonyScore) { this.harmonyScore = harmonyScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
