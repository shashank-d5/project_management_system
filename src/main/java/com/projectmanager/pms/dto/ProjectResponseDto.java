package com.projectmanager.pms.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for project response data
 * Contains all project information for client consumption
 */
public class ProjectResponseDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Owner information
    private Long ownerId;
    private String ownerName;
    private String ownerEmail;

    // Project statistics
    private Integer memberCount;
    private Integer taskCount;
    private Double completionPercentage;
    private Long completedTasks;
    private Long pendingTasks;

    // Project members (optional - for detailed view)
    private List<Map<String, Object>> members;

    // Project health indicators
    private Boolean isOverdue;
    private Long daysUntilDeadline;

    // Constructors
    public ProjectResponseDto() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(Integer taskCount) {
        this.taskCount = taskCount;
    }

    public Double getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Double completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public Long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(Long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public Long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(Long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public List<Map<String, Object>> getMembers() {
        return members;
    }

    public void setMembers(List<Map<String, Object>> members) {
        this.members = members;
    }

    public Boolean getIsOverdue() {
        return isOverdue;
    }

    public void setIsOverdue(Boolean isOverdue) {
        this.isOverdue = isOverdue;
    }

    public Long getDaysUntilDeadline() {
        return daysUntilDeadline;
    }

    public void setDaysUntilDeadline(Long daysUntilDeadline) {
        this.daysUntilDeadline = daysUntilDeadline;
    }

    @Override
    public String toString() {
        return "ProjectResponseDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                ", ownerId=" + ownerId +
                ", ownerName='" + ownerName + '\'' +
                ", memberCount=" + memberCount +
                ", taskCount=" + taskCount +
                ", completionPercentage=" + completionPercentage +
                '}';
    }
}