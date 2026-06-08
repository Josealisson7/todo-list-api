package com.stefanini.todo.web.dto;

import java.time.LocalDateTime;

public record TaskResponseDTO(
        Long taskId,
        String title,
        String description,
        LocalDateTime createdAt,
        String status
) {}
