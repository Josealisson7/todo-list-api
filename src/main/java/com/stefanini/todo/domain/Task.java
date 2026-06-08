package com.stefanini.todo.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private Long taskId;
    private String title;
    private String description;
    private LocalDateTime createdAt;
    private TaskStatus status;
}