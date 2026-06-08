package com.stefanini.todo.web.mapper;

import com.stefanini.todo.application.dto.CreateTaskInput;
import com.stefanini.todo.application.dto.TaskOutput;
import com.stefanini.todo.web.dto.TaskRequestDTO;
import com.stefanini.todo.web.dto.TaskResponseDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WebTaskMapper {

    CreateTaskInput toCreateInput(TaskRequestDTO dto);

    TaskResponseDTO toResponse(TaskOutput output);
}

