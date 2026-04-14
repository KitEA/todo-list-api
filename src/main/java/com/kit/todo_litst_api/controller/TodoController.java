package com.kit.todo_litst_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TodoResponse createTodo(
            @Valid @RequestBody TodoRequest request,
            @AuthenticationPrincipal Long userId) {
        return todoService.createTodo(request, userId);
    }
}
