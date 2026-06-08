package com.stefanini.todo.web.dto;

import jakarta.validation.constraints.Pattern;

public record TaskRequestDTO(
        String title,
        String description,
        @Pattern(regexp = "pending|progress|completed",
                message = "The status should be pending, in progress, or completed")
        String status
) {}
