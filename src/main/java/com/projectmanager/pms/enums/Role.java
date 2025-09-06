package com.projectmanager.pms.enums;

/**
 * Enum defining user roles in the system
 * - ADMIN: Can create/manage projects, manage all users
 * - USER: Can join projects, manage assigned tasks
 */
public enum Role {
    ADMIN("ADMIN", "System Administrator - Full Access"),
    USER("USER", "Regular User - Project Member");

    private final String roleName;
    private final String description;

    /**
     * Constructor for Role enum
     * @param roleName - the role name (used in security)
     * @param description - human-readable description
     */
    Role(String roleName, String description) {
        this.roleName = roleName;
        this.description = description;
    }

    public String getRoleName() {
        return roleName;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get role name with ROLE_ prefix (Spring Security convention)
     * @return Role name with ROLE_ prefix
     */
    public String getAuthority() {
        return "ROLE_" + roleName;
    }
}