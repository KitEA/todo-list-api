package com.kit.todo_litst_api.dto;

import java.util.Map;

public record ValidationErrorResponse(
        String code,
        String message,
        Map<String, String> errors
) {}
