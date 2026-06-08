package com.stefanini.todo.application.service;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;
import com.stefanini.todo.application.mapper.ApplicationTaskMapper;
import com.stefanini.todo.application.port.in.TaskUseCase;
import com.stefanini.todo.domain.Task;
import com.stefanini.todo.domain.TaskRepository;
import com.stefanini.todo.domain.TaskStatus;
import com.stefanini.todo.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaskService implements TaskUseCase {

    private final TaskRepository repository;
    private final ApplicationTaskMapper mapper;

    public TaskService(TaskRepository repository, ApplicationTaskMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public TaskOutput create(CreateTaskInput input) {
        Task task = mapper.toDomain(input);
        task.setCreatedAt(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        Task saved = repository.save(task);
        return mapper.toOutput(saved);
    }

    @Override
    public TaskOutput getById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        return toOutput(task);
    }

    @Override
    public List<TaskOutput> getAll() {
        return repository.findAll().stream().map(this::toOutput).toList();
    }

    @Override
    public TaskOutput update(Long id, CreateTaskInput input) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        mapper.updateDomainFromInput(input, task);
        Task saved = repository.save(task);
        return mapper.toOutput(saved);
    }

    @Override
    public TaskOutput updateStatus(Long id, String status) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task with id " + id + " not found"));
        task.setStatus(TaskStatus.from(status));
        Task saved = repository.save(task);
        return toOutput(saved);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<TaskOutput> listByStatus(String status) {
        return repository.findByStatus(status).stream().map(this::toOutput).toList();
    }

    private TaskOutput toOutput(Task task) {
        return new TaskOutput(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getCreatedAt(),
                task.getStatus() == null ? null : task.getStatus().value()
        );
    }
}
