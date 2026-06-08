package com.stefanini.todo.application.mapper;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;
import com.stefanini.todo.domain.Task;
import com.stefanini.todo.domain.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ApplicationTaskMapper {

    @Mapping(target = "taskId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "status", source = "status", qualifiedByName = "toTaskStatus")
    Task toDomain(CreateTaskInput input);

    @Mapping(target = "status", source = "status", qualifiedByName = "fromTaskStatus")
    TaskOutput toOutput(Task task);

    @Mapping(target = "status", source = "status", qualifiedByName = "toTaskStatus")
    void updateDomainFromInput(CreateTaskInput input, @MappingTarget Task task);

    @Named("toTaskStatus")
    default TaskStatus toTaskStatus(String status) {
        return status == null ? null : TaskStatus.from(status);
    }

    @Named("fromTaskStatus")
    default String fromTaskStatus(TaskStatus status) {
        return status == null ? null : status.value();
    }
}
