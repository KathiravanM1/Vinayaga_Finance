package com.example.Vinayaga.mapper;

import com.example.Vinayaga.dto.request.TodoRequest;
import com.example.Vinayaga.dto.response.TodoResponse;
import com.example.Vinayaga.entity.Todo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TodoMapper {

    @Mapping(target = "todoId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Todo toEntity(TodoRequest request);

    TodoResponse toResponse(Todo todo);
}
