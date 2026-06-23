package com.example.Vinayaga.controller;

import com.example.Vinayaga.dto.request.TodoRequest;
import com.example.Vinayaga.dto.response.ApiResponse;
import com.example.Vinayaga.dto.response.PagedResponse;
import com.example.Vinayaga.dto.response.TodoResponse;
import com.example.Vinayaga.service.TodoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@Validated
public class TodoController {

    private final TodoService todoService;

    @PostMapping
    public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
            @Valid @RequestBody TodoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Todo created successfully", todoService.createTodo(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TodoResponse>>> getAllTodos(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        return ResponseEntity.ok(ApiResponse.success(todoService.getAllTodos(status, page, size)));
    }

    @GetMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(@PathVariable Long todoId) {
        return ResponseEntity.ok(ApiResponse.success(todoService.getTodoById(todoId)));
    }

    @PutMapping("/{todoId}")
    public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
            @PathVariable Long todoId,
            @Valid @RequestBody TodoRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Todo updated successfully", todoService.updateTodo(todoId, request)));
    }

    @PatchMapping("/{todoId}/status")
    public ResponseEntity<ApiResponse<TodoResponse>> updateStatus(
            @PathVariable Long todoId,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success("Todo status updated", todoService.updateStatus(todoId, status)));
    }

    @DeleteMapping("/{todoId}")
    public ResponseEntity<ApiResponse<Void>> deleteTodo(@PathVariable Long todoId) {
        todoService.deleteTodo(todoId);
        return ResponseEntity.ok(ApiResponse.success("Todo deleted successfully", null));
    }
}
