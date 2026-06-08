package com.stefanini.todo.application;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;
import com.stefanini.todo.application.mapper.ApplicationTaskMapper;
import com.stefanini.todo.application.service.TaskService;
import com.stefanini.todo.domain.Task;
import com.stefanini.todo.domain.TaskRepository;
import com.stefanini.todo.domain.TaskStatus;
import com.stefanini.todo.web.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @Mock
    private ApplicationTaskMapper mapper;

    @InjectMocks
    private TaskService service;

    @Captor
    private ArgumentCaptor<Task> taskCaptor;

    private static final LocalDateTime CREATED_AT = LocalDateTime.of(2026, 6, 7, 2, 30);

    @BeforeEach
    void setup() {
    }

    @Test
    void shouldReturnAllTasks() {
        Task task = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);

        when(repository.findAll()).thenReturn(List.of(task));

        List<TaskOutput> result = service.getAll();

        assertEquals(1, result.size());
        TaskOutput out = result.get(0);
        assertEquals(1L, out.taskId());
        assertEquals("Study SQL", out.title());
        assertEquals("Finish module", out.description());
        assertEquals(CREATED_AT, out.createdAt());
        assertEquals("PENDING", out.status());

        verify(repository, times(1)).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnTaskById() {
        Task task = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(task));

        TaskOutput result = service.getById(1L);

        assertEquals(1L, result.taskId());
        assertEquals("Study SQL", result.title());
        assertEquals("Finish module", result.description());
        assertEquals(CREATED_AT, result.createdAt());
        assertEquals("PENDING", result.status());

        verify(repository, times(1)).findById(1L);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldThrowExceptionWhenTaskNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(99L));
        verify(repository, times(1)).findById(99L);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldCreateTask() {
        CreateTaskInput input = new CreateTaskInput("Study SQL", "Finish module", "PENDING");
        Task domainToSave = new Task(null, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);
        Task saved = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);
        TaskOutput output = new TaskOutput(1L, "Study SQL", "Finish module", CREATED_AT, "PENDING");

        when(mapper.toDomain(input)).thenReturn(domainToSave);
        when(repository.save(domainToSave)).thenReturn(saved);
        when(mapper.toOutput(saved)).thenReturn(output);

        TaskOutput result = service.create(input);

        assertNotNull(result);
        assertEquals(1L, result.taskId());
        assertEquals("Study SQL", result.title());

        verify(mapper, times(1)).toDomain(input);
        verify(repository, times(1)).save(domainToSave);
        verify(mapper, times(1)).toOutput(saved);
    }

    @Test
    void shouldVerifyRepositorySaveCalledWithMappedDomainOnCreate() {
        CreateTaskInput input = new CreateTaskInput("Study SQL", "Finish module", "PENDING");
        Task mapped = new Task(null, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);
        Task saved = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);
        TaskOutput output = new TaskOutput(1L, "Study SQL", "Finish module", CREATED_AT, "PENDING");

        when(mapper.toDomain(input)).thenReturn(mapped);
        when(repository.save(any(Task.class))).thenReturn(saved);
        when(mapper.toOutput(saved)).thenReturn(output);

        TaskOutput result = service.create(input);

        assertNotNull(result);
        verify(mapper).toDomain(input);
        verify(repository).save(taskCaptor.capture());
        Task captured = taskCaptor.getValue();
        assertEquals("Study SQL", captured.getTitle());
        assertEquals(TaskStatus.PENDING, captured.getStatus());
    }

    @Test
    void shouldUpdateTask() {
        CreateTaskInput input = new CreateTaskInput("Study Spring", "Finish module", "PROGRESS");
        Task existing = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);
        Task updated = new Task(1L, "Study Spring", "Finish module", CREATED_AT, TaskStatus.PROGRESS);
        TaskOutput output = new TaskOutput(1L, "Study Spring", "Finish module", CREATED_AT, "PROGRESS");

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        doAnswer(invocation -> {
            CreateTaskInput req = invocation.getArgument(0);
            Task t = invocation.getArgument(1);
            t.setTitle(req.title());
            t.setDescription(req.description());
            t.setStatus(TaskStatus.from(req.status()));
            return null;
        }).when(mapper).updateDomainFromInput(eq(input), eq(existing));

        when(repository.save(existing)).thenReturn(updated);
        when(mapper.toOutput(updated)).thenReturn(output);

        TaskOutput result = service.update(1L, input);

        assertEquals("Study Spring", result.title());
        assertEquals("PROGRESS", result.status());
        verify(repository).findById(1L);
        verify(mapper).updateDomainFromInput(input, existing);
        verify(repository).save(existing);
        verify(mapper).toOutput(updated);
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksExist() {
        when(repository.findAll()).thenReturn(List.of());

        List<TaskOutput> result = service.getAll();

        assertTrue(result.isEmpty());
        verify(repository).findAll();
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldThrowExceptionWhenCreatingTaskWithInvalidStatus() {
        CreateTaskInput input = new CreateTaskInput("Study SQL", "Finish module", "invalid");

        when(mapper.toDomain(input)).thenThrow(new IllegalArgumentException("Invalid status"));

        assertThrows(IllegalArgumentException.class, () -> service.create(input));
        verify(mapper).toDomain(input);
        verifyNoInteractions(repository);
    }

    @Test
    void shouldKeepTaskUnchangedWhenUpdateDoesNotModifyAnything() {
        CreateTaskInput input = new CreateTaskInput("Study SQL", "Finish module", "PENDING");
        Task existing = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);

        when(repository.findById(1L)).thenReturn(Optional.of(existing));
        when(repository.save(existing)).thenReturn(existing);
        doNothing().when(mapper).updateDomainFromInput(eq(input), eq(existing));
        when(mapper.toOutput(existing)).thenReturn(new TaskOutput(1L, "Study SQL", "Finish module", CREATED_AT, "PENDING"));

        TaskOutput result = service.update(1L, input);

        assertEquals("Study SQL", result.title());
        assertEquals("PENDING", result.status());
        verify(repository).findById(1L);
        verify(repository).save(existing);
        verify(mapper).updateDomainFromInput(input, existing);
        verify(mapper).toOutput(existing);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistentTask() {
        doThrow(new ResourceNotFoundException("Task not found"))
                .when(repository).deleteById(99L);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(99L));
        verify(repository).deleteById(99L);
    }

    @Test
    void shouldDeleteTask() {
        doNothing().when(repository).deleteById(1L);

        service.delete(1L);

        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void shouldReturnTasksByStatus() {
        Task task = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);

        when(repository.findByStatus("PENDING")).thenReturn(List.of(task));

        List<TaskOutput> result = service.listByStatus("PENDING");

        assertEquals(1, result.size());
        TaskOutput out = result.get(0);
        assertEquals("PENDING", out.status());
        assertEquals("Study SQL", out.title());
        verify(repository).findByStatus("PENDING");
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldReturnEmptyListWhenNoTasksForStatus() {
        when(repository.findByStatus("COMPLETED")).thenReturn(List.of());

        List<TaskOutput> result = service.listByStatus("COMPLETED");

        assertTrue(result.isEmpty());
        verify(repository).findByStatus("COMPLETED");
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldUpdateTaskStatusSuccessfully() {
        Task task = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.PENDING);
        Task updated = new Task(1L, "Study SQL", "Finish module", CREATED_AT, TaskStatus.COMPLETED);

        when(repository.findById(1L)).thenReturn(Optional.of(task));
        when(repository.save(task)).thenReturn(updated);

        TaskOutput result = service.updateStatus(1L, "COMPLETED");

        assertEquals("COMPLETED", result.status());
        verify(repository).findById(1L);
        verify(repository).save(task);
        verifyNoInteractions(mapper);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingStatusForNonExistentTask() {
        when(repository.findById(42L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updateStatus(42L, "COMPLETED"));
        verify(repository).findById(42L);
        verifyNoMoreInteractions(repository);
        verifyNoInteractions(mapper);
    }
}
