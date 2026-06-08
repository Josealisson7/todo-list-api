package com.stefanini.todo.application.port.in;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;

import java.util.List;

public interface TaskUseCase {
    TaskOutput create(CreateTaskInput input);
    TaskOutput getById(Long id);
    List<TaskOutput> getAll();
    TaskOutput update(Long id, CreateTaskInput input);
    TaskOutput updateStatus(Long id, String status);
    void delete(Long id);
    List<TaskOutput> listByStatus(String status);
}

