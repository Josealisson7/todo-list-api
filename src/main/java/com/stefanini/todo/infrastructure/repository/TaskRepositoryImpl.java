package com.stefanini.todo.infrastructure.repository;

import com.stefanini.todo.domain.Task;
import com.stefanini.todo.domain.TaskRepository;
import com.stefanini.todo.infrastructure.entity.TaskEntity;
import com.stefanini.todo.infrastructure.mapper.TaskEntityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private final SpringDataTaskRepository jpaRepository;
    private final TaskEntityMapper mapper;

    @Autowired
    public TaskRepositoryImpl(SpringDataTaskRepository jpaRepository, TaskEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Task save(Task task) {
        TaskEntity entity = mapper.toEntity(task);
        TaskEntity savedEntity = jpaRepository.saveAndFlush(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Task> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Task> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<Task> findByStatus(String status) {
        return jpaRepository.findByStatus(status)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
