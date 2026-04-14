package com.kit.todo_litst_api.dto;

import com.kit.todo_litst_api.model.Todo;

public record TodoResponse(
        Long id,
        String title,
        String description
) {
    public static TodoResponse mapEntityToDto(Todo todo) {
        return new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription());
    }
}
