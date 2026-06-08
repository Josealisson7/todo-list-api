package com.stefanini.todo.infrastructure;

import com.stefanini.todo.domain.Task;
import com.stefanini.todo.domain.TaskRepository;
import com.stefanini.todo.domain.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class TaskRepositoryImplIntegrationTest {

    @Autowired
    private TaskRepository repository;

    @Autowired
    private DataSource dataSource;

    @Test
    void saveAndFindById_shouldPersistAndReturn() {
        Task task = new Task(null, "Integration Test", "desc", LocalDateTime.now().withNano(0), TaskStatus.PENDING);
        Task saved = repository.save(task);
        Optional<Task> found = repository.findById(saved.getTaskId());

        assertNotNull(saved.getTaskId());
        assertTrue(found.isPresent());
        assertEquals(saved.getTaskId(), found.get().getTaskId());
        assertEquals("Integration Test", found.get().getTitle());
    }

    @Test
    void deleteById_shouldRemoveTask() {
        Task task = new Task(null, "To be deleted", "will be removed", LocalDateTime.now().withNano(0), TaskStatus.PENDING);
        Task saved = repository.save(task);
        Long id = saved.getTaskId();
        assertNotNull(id);

        repository.deleteById(id);

        Optional<Task> found = repository.findById(id);
        assertFalse(found.isPresent());
    }

    @Test
    void findByStatus_shouldReturnSavedTasks() {
        Task t1 = new Task(null, "Task Pending 1", "d1", LocalDateTime.now().withNano(0), TaskStatus.PENDING);
        Task t2 = new Task(null, "Task Progress", "d2", LocalDateTime.now().withNano(0), TaskStatus.PROGRESS);
        Task t3 = new Task(null, "Task Pending 2", "d3", LocalDateTime.now().withNano(0), TaskStatus.PENDING);

        Task saved1 = repository.save(t1);
        repository.save(t2);
        Task saved3 = repository.save(t3);

        List<Task> pending = repository.findByStatus("PENDING");

        assertFalse(pending.isEmpty());
        assertTrue(pending.stream().anyMatch(t -> t.getTaskId().equals(saved1.getTaskId())));
        assertTrue(pending.stream().anyMatch(t -> t.getTaskId().equals(saved3.getTaskId())));
        assertTrue(pending.stream().noneMatch(t -> t.getStatus() == TaskStatus.PROGRESS));
    }

}