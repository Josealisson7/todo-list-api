package com.stefanini.todo.infrastructure.mapper;

import com.stefanini.todo.domain.Task;
import com.stefanini.todo.infrastructure.entity.TaskEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TaskEntityMapper {

    TaskEntity toEntity(Task domain);

    Task toDomain(TaskEntity entity);
}

