package com.kaapstyle.kids.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kids_profiles")
public class KidsProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "guardian_id", nullable = false)
    private Long guardianId;

    @Column(name = "child_name", nullable = false, length = 100)
    private String childName;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "school_name", length = 150)
    private String schoolName;

    @Column(name = "grade_level", length = 30)
    private String gradeLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public KidsProfile() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getGuardianId() { return guardianId; }
    public void setGuardianId(Long guardianId) { this.guardianId = guardianId; }

    public String getChildName() { return childName; }
    public void setChildName(String childName) { this.childName = childName; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getGradeLevel() { return gradeLevel; }
    public void setGradeLevel(String gradeLevel) { this.gradeLevel = gradeLevel; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
