package com.stefanini.todo.domain;

public enum TaskStatus {
    PENDING("pending"),
    PROGRESS("progress"),
    COMPLETED("completed");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static TaskStatus from(String value) {
        if (value == null) return null;
        return switch (value.toLowerCase()) {
            case "pending" -> PENDING;
            case "progress" -> PROGRESS;
            case "completed" -> COMPLETED;
            default -> throw new IllegalArgumentException("Invalid status: " + value);
        };
    }
}

