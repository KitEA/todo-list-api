package com.kit.todo_litst_api.dto;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> data,
        int page,
        int limit,
        long total
) {
}
