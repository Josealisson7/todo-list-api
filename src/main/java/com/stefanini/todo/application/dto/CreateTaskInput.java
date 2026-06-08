package com.stefanini.todo.application.dto;

public record CreateTaskInput(
        String title,
        String description,
        String status
) {}

