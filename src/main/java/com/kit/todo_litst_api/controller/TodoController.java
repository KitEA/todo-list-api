package com.kit.todo_litst_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.kit.todo_litst_api.dto.PaginatedResponse;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.service.TodoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping
    public PaginatedResponse<TodoResponse> getTodos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal Long userId) {
        return todoService.getTodos(userId, page, limit);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal Long userId) {
        return todoService.createTodo(request, userId);
    }

    @PutMapping("/{id}")
    public TodoResponse updateTodo(
            @PathVariable Long id,
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal Long userId) {
        return todoService.updateTodo(id, request, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
            @PathVariable Long id,
            @AuthenticationPrincipal Long userId) {
        todoService.deleteTodo(id, userId);
    }
}
