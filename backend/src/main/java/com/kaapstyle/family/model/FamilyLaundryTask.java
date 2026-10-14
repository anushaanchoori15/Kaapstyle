package com.kaapstyle.family.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "family_laundry_tasks")
public class FamilyLaundryTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "family_group_id", nullable = false)
    private Long familyGroupId;

    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    @Column(name = "task_title", nullable = false, length = 150)
    private String taskTitle;

    @Column(name = "item_count", nullable = false)
    private Integer itemCount = 1;

    @Column(name = "task_status", nullable = false, length = 30)
    private String taskStatus = "PENDING"; // PENDING, IN_PROGRESS, COMPLETED

    @Column(name = "due_date")
    private LocalDate dueDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public FamilyLaundryTask() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFamilyGroupId() { return familyGroupId; }
    public void setFamilyGroupId(Long familyGroupId) { this.familyGroupId = familyGroupId; }

    public Long getAssignedUserId() { return assignedUserId; }
    public void setAssignedUserId(Long assignedUserId) { this.assignedUserId = assignedUserId; }

    public String getTaskTitle() { return taskTitle; }
    public void setTaskTitle(String taskTitle) { this.taskTitle = taskTitle; }

    public Integer getItemCount() { return itemCount; }
    public void setItemCount(Integer itemCount) { this.itemCount = itemCount; }

    public String getTaskStatus() { return taskStatus; }
    public void setTaskStatus(String taskStatus) { this.taskStatus = taskStatus; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
