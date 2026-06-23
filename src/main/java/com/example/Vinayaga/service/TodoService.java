package com.example.Vinayaga.service;

import com.example.Vinayaga.dto.request.TodoRequest;
import com.example.Vinayaga.dto.response.PagedResponse;
import com.example.Vinayaga.dto.response.TodoResponse;
import com.example.Vinayaga.entity.Todo;
import com.example.Vinayaga.exception.BusinessValidationException;
import com.example.Vinayaga.exception.ResourceNotFoundException;
import com.example.Vinayaga.mapper.TodoMapper;
import com.example.Vinayaga.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoService {

    private static final Logger log = LoggerFactory.getLogger(TodoService.class);

    private final TodoRepository todoRepository;
    private final TodoMapper todoMapper;

    @Transactional
    public TodoResponse createTodo(TodoRequest request) {
        Todo todo = todoMapper.toEntity(request);
        todo.setStatus(request.getStatus() != null ? request.getStatus() : "PENDING");
        todo.setPriority(request.getPriority() != null ? request.getPriority() : "MEDIUM");
        todo.setCreatedAt(LocalDateTime.now());
        todo.setUpdatedAt(LocalDateTime.now());
        Todo saved = todoRepository.save(todo);
        log.info("Todo created: id={} title='{}'", saved.getTodoId(), saved.getTitle());
        return todoMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public PagedResponse<TodoResponse> getAllTodos(String status, int page, int size) {
        if (status != null && !status.isBlank()) {
            validateStatus(status);
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Todo> todoPage = (status != null && !status.isBlank())
                ? todoRepository.findByStatus(status.toUpperCase(), pageable)
                : todoRepository.findAll(pageable);
        return buildPagedResponse(todoPage);
    }

    @Transactional(readOnly = true)
    public TodoResponse getTodoById(Long todoId) {
        return todoMapper.toResponse(findOrThrow(todoId));
    }

    @Transactional
    public TodoResponse updateTodo(Long todoId, TodoRequest request) {
        Todo todo = findOrThrow(todoId);
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        if (request.getStatus() != null) todo.setStatus(request.getStatus());
        if (request.getPriority() != null) todo.setPriority(request.getPriority());
        todo.setDueDate(request.getDueDate());
        todo.setUpdatedAt(LocalDateTime.now());
        return todoMapper.toResponse(todoRepository.save(todo));
    }

    @Transactional
    public void deleteTodo(Long todoId) {
        findOrThrow(todoId);
        todoRepository.deleteById(todoId);
        log.info("Todo deleted: id={}", todoId);
    }

    @Transactional
    public TodoResponse updateStatus(Long todoId, String status) {
        validateStatus(status);
        Todo todo = findOrThrow(todoId);
        todo.setStatus(status.toUpperCase());
        todo.setUpdatedAt(LocalDateTime.now());
        return todoMapper.toResponse(todoRepository.save(todo));
    }

    private Todo findOrThrow(Long todoId) {
        return todoRepository.findById(todoId)
                .orElseThrow(() -> new ResourceNotFoundException("todo", todoId));
    }

    private void validateStatus(String status) {
        List<String> allowed = List.of("PENDING", "IN_PROGRESS", "DONE");
        if (!allowed.contains(status.toUpperCase())) {
            throw new BusinessValidationException(
                    "Invalid status '" + status + "'. Must be one of: " + allowed);
        }
    }

    private PagedResponse<TodoResponse> buildPagedResponse(Page<Todo> page) {
        return PagedResponse.<TodoResponse>builder()
                .content(page.getContent().stream().map(todoMapper::toResponse).toList())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .build();
    }
}
