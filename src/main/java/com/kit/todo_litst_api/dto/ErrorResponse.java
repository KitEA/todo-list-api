package com.kit.todo_litst_api.dto;

public record ErrorResponse(
        String code,
        String message
) {}
