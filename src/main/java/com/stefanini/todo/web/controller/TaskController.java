package com.stefanini.todo.web.controller;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;
import com.stefanini.todo.application.port.in.TaskUseCase;
import com.stefanini.todo.web.dto.TaskRequestDTO;
import com.stefanini.todo.web.dto.TaskResponseDTO;
import com.stefanini.todo.web.mapper.WebTaskMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Validated
public class TaskController {

    private final TaskUseCase taskUseCase;
    private final WebTaskMapper webTaskMapper;

    public TaskController(TaskUseCase taskUseCase, WebTaskMapper webTaskMapper) {
        this.taskUseCase = taskUseCase;
        this.webTaskMapper = webTaskMapper;
    }

    @GetMapping
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<TaskResponseDTO> dtos = taskUseCase.getAll().stream().map(webTaskMapper::toResponse).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        TaskOutput out = taskUseCase.getById(id);
        return ResponseEntity.ok(webTaskMapper.toResponse(out));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        CreateTaskInput input = webTaskMapper.toCreateInput(dto);
        TaskOutput created = taskUseCase.create(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(webTaskMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody TaskRequestDTO dto) {
        CreateTaskInput input = webTaskMapper.toCreateInput(dto);
        TaskOutput updated = taskUseCase.update(id, input);
        return ResponseEntity.ok(webTaskMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteTask(@PathVariable Long id) {
        taskUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByStatus(@PathVariable String status) {
        List<TaskResponseDTO> tasks = taskUseCase.listByStatus(status).stream().map(webTaskMapper::toResponse).toList();
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        String status = body.get("status");
        TaskOutput updated = taskUseCase.updateStatus(id, status);
        return ResponseEntity.ok(webTaskMapper.toResponse(updated));
    }
}

