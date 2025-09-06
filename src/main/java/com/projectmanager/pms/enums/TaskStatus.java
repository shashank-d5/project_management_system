package com.projectmanager.pms.enums;

/**
 * Enum defining task statuses for Kanban board
 * These will be used as columns in our Kanban interface
 */
public enum TaskStatus {
    TODO("TO_DO", "To Do", "Tasks that haven't been started yet"),
    IN_PROGRESS("IN_PROGRESS", "In Progress", "Tasks currently being worked on"),
    DONE("DONE", "Done", "Completed tasks");

    private final String value;
    private final String displayName;
    private final String description;

    /**
     * Constructor for TaskStatus enum
     * @param value - database value
     * @param displayName - UI display name
     * @param description - description for tooltip/help
     */
    TaskStatus(String value, String displayName, String description) {
        this.value = value;
        this.displayName = displayName;
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}