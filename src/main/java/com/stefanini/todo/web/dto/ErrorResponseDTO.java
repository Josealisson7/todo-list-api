package com.stefanini.todo.web.dto;

public record ErrorResponseDTO(
        int status,
        String error
) {}
