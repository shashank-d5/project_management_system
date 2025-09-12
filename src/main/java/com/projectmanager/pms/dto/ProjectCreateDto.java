package com.projectmanager.pms.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * DTO for project creation requests
 * Contains validation annotations for input validation
 */
public class ProjectCreateDto {

    @NotBlank(message = "Project name is required")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private LocalDate startDate;

    private LocalDate endDate;

    // Constructors
    public ProjectCreateDto() {}

    public ProjectCreateDto(String name, String description, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
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

    // Validation Methods
    /**
     * Check if end date is after start date
     * @return true if dates are valid
     */
    public boolean isDateRangeValid() {
        if (startDate == null || endDate == null) {
            return true; // Allow null dates
        }
        return !endDate.isBefore(startDate);
    }

    @Override
    public String toString() {
        return "ProjectCreateDto{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}