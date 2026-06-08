package com.stefanini.todo.infrastructure.repository;

import com.stefanini.todo.infrastructure.entity.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataTaskRepository extends JpaRepository<TaskEntity, Long> {
    List<TaskEntity> findByStatus(String status);
}

