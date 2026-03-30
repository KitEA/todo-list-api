package com.kit.todo_litst_api.dto;

import jakarta.validation.constraints.NotBlank;

public record TodoRequest(
        @NotBlank(message = "Title is required")
        String title,
        
        String description
) {
}
