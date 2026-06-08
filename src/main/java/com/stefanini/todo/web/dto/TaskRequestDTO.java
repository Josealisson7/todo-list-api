package com.stefanini.todo.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record TaskRequestDTO(
        @NotBlank
        String title,
        String description,
        @Pattern(regexp = "PENDING|PROGRESS|COMPLETED",
                message = "The status should be PENDING, in PROGRESS, or COMPLETED")
        String status
) {}
