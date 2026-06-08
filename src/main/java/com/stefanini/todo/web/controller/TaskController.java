package com.stefanini.todo.web.controller;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;
import com.stefanini.todo.application.port.in.TaskUseCase;
import com.stefanini.todo.web.dto.StatusRequestDTO;
import com.stefanini.todo.web.dto.TaskRequestDTO;
import com.stefanini.todo.web.dto.TaskResponseDTO;
import com.stefanini.todo.web.mapper.WebTaskMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @Operation(summary = "Listar todas as tasks", description = "Retorna todas as tarefas cadastradas")
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        List<TaskResponseDTO> dtos = taskUseCase.getAll().stream().map(webTaskMapper::toResponse).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar task por id", description = "Retorna uma tarefa pelo seu identificador")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id) {
        TaskOutput out = taskUseCase.getById(id);
        return ResponseEntity.ok(webTaskMapper.toResponse(out));
    }

    @PostMapping
    @Operation(summary = "Criar nova task", description = "Cria uma nova tarefa com título, descrição e status")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Task criada com sucesso")
    })
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO dto) {
        CreateTaskInput input = webTaskMapper.toCreateInput(dto);
        TaskOutput created = taskUseCase.create(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(webTaskMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar task", description = "Atualiza título, descrição e status de uma task existente")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id,
                                                      @Valid @RequestBody TaskRequestDTO dto) {
        CreateTaskInput input = webTaskMapper.toCreateInput(dto);
        TaskOutput updated = taskUseCase.update(id, input);
        return ResponseEntity.ok(webTaskMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover task", description = "Remove uma task pelo id")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Task removida com sucesso")
    })
    public ResponseEntity deleteTask(@PathVariable Long id) {
        taskUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Listar tasks por status", description = "Retorna tasks filtradas por status (pending|progress|completed)")
    public ResponseEntity<List<TaskResponseDTO>> getTasksByStatus(@PathVariable String status) {
        List<TaskResponseDTO> tasks = taskUseCase.listByStatus(status).stream().map(webTaskMapper::toResponse).toList();
        return ResponseEntity.ok(tasks);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar apenas o status", description = "Atualiza somente o status de uma task")
    public ResponseEntity<TaskResponseDTO> updateTaskStatus(
            @PathVariable Long id,
            @RequestBody @Valid StatusRequestDTO statusRequestDTO) {

        String status = statusRequestDTO.status();
        TaskOutput updated = taskUseCase.updateStatus(id, status);
        return ResponseEntity.ok(webTaskMapper.toResponse(updated));
    }
}

