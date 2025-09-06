package com.projectmanager.pms.entity;

import com.projectmanager.pms.enums.TaskPriority;
import com.projectmanager.pms.enums.TaskStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Task Entity - Represents tasks within projects
 * Each task belongs to a project and can be assigned to a user
 */
@Entity
@Table(name = "tasks", indexes = {
        @Index(name = "idx_task_status", columnList = "status"),  // Index for filtering by status
        @Index(name = "idx_task_project", columnList = "project_id"),  // Index for project queries
        @Index(name = "idx_task_assignee", columnList = "assigned_to_id")  // Index for assignee queries
})
public class Task extends BaseEntity {

    @NotBlank(message = "Task title is required")
    @Size(min = 3, max = 150, message = "Task title must be between 3 and 150 characters")
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    @Column(name = "description", length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)  // Store enum as string in database
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.TODO;  // Default status

    @Enumerated(EnumType.STRING)  // Store enum as string in database
    @Column(name = "priority", nullable = false)
    private TaskPriority priority = TaskPriority.MEDIUM;  // Default priority

    @Column(name = "deadline")
    private LocalDate deadline;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "actual_hours")
    private Integer actualHours;

    // Many-to-One relationship with Project (each task belongs to one project)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    // Many-to-One relationship with User (assigned user)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    private User assignedTo;

    // Many-to-One relationship with User (task creator)
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    // Constructors
    public Task() {}

    public Task(String title, String description, Project project, User createdBy) {
        this.title = title;
        this.description = description;
        this.project = project;
        this.createdBy = createdBy;
    }

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public TaskPriority getPriority() {
        return priority;
    }

    public void setPriority(TaskPriority priority) {
        this.priority = priority;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public Integer getEstimatedHours() {
        return estimatedHours;
    }

    public void setEstimatedHours(Integer estimatedHours) {
        this.estimatedHours = estimatedHours;
    }

    public Integer getActualHours() {
        return actualHours;
    }

    public void setActualHours(Integer actualHours) {
        this.actualHours = actualHours;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public User getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(User assignedTo) {
        this.assignedTo = assignedTo;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    // Utility Methods
    /**
     * Check if task is overdue
     * @return true if deadline has passed and task is not completed
     */
    public boolean isOverdue() {
        return deadline != null &&
                deadline.isBefore(LocalDate.now()) &&
                status != TaskStatus.DONE;
    }

    /**
     * Check if task is assigned to someone
     * @return true if task has an assignee
     */
    public boolean isAssigned() {
        return assignedTo != null;
    }

    /**
     * Check if task is completed
     * @return true if status is DONE
     */
    public boolean isCompleted() {
        return status == TaskStatus.DONE;
    }

    /**
     * Get days remaining until deadline
     * @return number of days (negative if overdue, null if no deadline)
     */
    public Long getDaysUntilDeadline() {
        if (deadline == null) return null;
        return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);
    }

    /**
     * Move task to next status (TODO -> IN_PROGRESS -> DONE)
     */
    public void moveToNextStatus() {
        switch (status) {
            case TODO:
                status = TaskStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                status = TaskStatus.DONE;
                break;
            case DONE:
                // Already completed, no next status
                break;
        }
    }

    /**
     * Move task to previous status (DONE -> IN_PROGRESS -> TODO)
     */
    public void moveToPreviousStatus() {
        switch (status) {
            case DONE:
                status = TaskStatus.IN_PROGRESS;
                break;
            case IN_PROGRESS:
                status = TaskStatus.TODO;
                break;
            case TODO:
                // Already at first status
                break;
        }
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", title='" + title + '\'' +
                ", status=" + status +
                ", priority=" + priority +
                ", deadline=" + deadline +
                ", project=" + (project != null ? project.getName() : "null") +
                ", assignedTo=" + (assignedTo != null ? assignedTo.getFullName() : "Unassigned") +
                ", createdBy=" + (createdBy != null ? createdBy.getFullName() : "null") +
                '}';
    }
}