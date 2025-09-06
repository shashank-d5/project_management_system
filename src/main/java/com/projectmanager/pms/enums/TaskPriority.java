package com.projectmanager.pms.enums;

/**
 * Enum defining task priorities
 * Used for task sorting and visual indicators in UI
 */
public enum TaskPriority {
    LOW(1, "LOW", "Low Priority", "#28a745"),        // Green
    MEDIUM(2, "MEDIUM", "Medium Priority", "#ffc107"), // Yellow
    HIGH(3, "HIGH", "High Priority", "#fd7e14"),       // Orange
    URGENT(4, "URGENT", "Urgent Priority", "#dc3545"); // Red

    private final int level;
    private final String value;
    private final String displayName;
    private final String colorCode;

    /**
     * Constructor for TaskPriority enum
     * @param level - numeric level for sorting (1=lowest, 4=highest)
     * @param value - database value
     * @param displayName - UI display name
     * @param colorCode - hex color code for UI styling
     */
    TaskPriority(int level, String value, String displayName, String colorCode) {
        this.level = level;
        this.value = value;
        this.displayName = displayName;
        this.colorCode = colorCode;
    }

    public int getLevel() {
        return level;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getColorCode() {
        return colorCode;
    }
}