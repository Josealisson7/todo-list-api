package com.stefanini.todo.domain;

public enum TaskStatus {
    PENDING("PENDING"),
    PROGRESS("PROGRESS"),
    COMPLETED("COMPLETED");

    private final String value;

    TaskStatus(String value) {
        this.value = value;
    }

    public String value() {
        return this.value;
    }

    public static TaskStatus from(String value) {
        if (value == null) return null;
        return switch (value) {
            case "PENDING" -> PENDING;
            case "PROGRESS" -> PROGRESS;
            case "COMPLETED" -> COMPLETED;
            default -> throw new IllegalArgumentException("Invalid status: " + value);
        };
    }

    @Override
    public String toString() {
        return value();
    }
}

