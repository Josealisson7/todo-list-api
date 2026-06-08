package com.stefanini.todo.application.dto;

import java.time.LocalDateTime;

public record TaskOutput(
        Long taskId,
        String title,
        String description,
        LocalDateTime createdAt,
        String status
) {}
