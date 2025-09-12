package com.projectmanager.pms.exception;

/**
 * Custom exception thrown when user doesn't have access to a project
 */
public class ProjectAccessDeniedException extends RuntimeException {

    public ProjectAccessDeniedException() {
        super("Access denied. You are not a member of this project.");
    }

    public ProjectAccessDeniedException(String message) {
        super(message);
    }

    public ProjectAccessDeniedException(String message, Throwable cause) {
        super(message, cause);
    }
}