package com.projectmanager.pms.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Project Entity - Represents projects in our system
 * Each project can have multiple users (team members) and multiple tasks
 */
@Entity
@Table(name = "projects")
public class Project extends BaseEntity {

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Many-to-One relationship with User (project owner)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    // Many-to-Many relationship with User (project members)
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "user_projects",  // Join table name
            joinColumns = @JoinColumn(name = "project_id"),  // Foreign key to Project
            inverseJoinColumns = @JoinColumn(name = "user_id")  // Foreign key to User
    )
    private Set<User> members = new HashSet<>();

    // One-to-Many relationship with Task
    @OneToMany(mappedBy = "project", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Task> tasks = new HashSet<>();

    // Constructors
    public Project() {}

    public Project(String name, String description, User owner) {
        this.name = name;
        this.description = description;
        this.owner = owner;
        this.members.add(owner);  // Owner is automatically a member
    }

    // Getters and Setters
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public Set<User> getMembers() {
        return members;
    }

    public void setMembers(Set<User> members) {
        this.members = members;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    // Utility Methods
    /**
     * Add a member to the project
     * @param user - user to add as member
     */
    public void addMember(User user) {
        members.add(user);
        user.getProjects().add(this);
    }

    /**
     * Remove a member from the project
     * @param user - user to remove from members
     */
    public void removeMember(User user) {
        members.remove(user);
        user.getProjects().remove(this);
    }

    /**
     * Check if user is a member of this project
     * @param user - user to check
     * @return true if user is a member
     */
    public boolean isMember(User user) {
        return members.contains(user);
    }

    /**
     * Check if user is the owner of this project
     * @param user - user to check
     * @return true if user is the owner
     */
    public boolean isOwner(User user) {
        return owner.equals(user);
    }

    /**
     * Get total number of tasks in this project
     * @return number of tasks
     */
    public int getTaskCount() {
        return tasks.size();
    }

    /**
     * Get number of completed tasks
     * @return number of completed tasks
     */
    public long getCompletedTaskCount() {
        return tasks.stream()
                .filter(task -> task.getStatus().name().equals("DONE"))
                .count();
    }

    /**
     * Get project completion percentage
     * @return completion percentage (0-100)
     */
    public double getCompletionPercentage() {
        if (tasks.isEmpty()) return 0.0;
        return (getCompletedTaskCount() * 100.0) / tasks.size();
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                ", owner=" + (owner != null ? owner.getFullName() : "null") +
                ", memberCount=" + members.size() +
                ", taskCount=" + tasks.size() +
                '}';
    }
}