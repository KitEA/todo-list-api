package com.kit.todo_litst_api.service;

import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.model.Todo;
import com.kit.todo_litst_api.model.User;
import com.kit.todo_litst_api.model.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;

    public TodoResponse createTodo(TodoRequest request, User user) {
        Todo todo = Todo.builder()
                .title(request.title())
                .description(request.description())
                .user(user)
                .build();

        todo = todoRepository.save(todo);

        return new TodoResponse(todo.getId(), todo.getTitle(), todo.getDescription());
    }
}
