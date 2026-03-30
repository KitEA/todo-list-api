package com.kit.todo_litst_api.dto;

public record TodoResponse(
        Long id,
        String title,
        String description
) {
}
