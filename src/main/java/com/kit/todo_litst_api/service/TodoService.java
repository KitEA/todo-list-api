package com.kit.todo_litst_api.service;

import org.springframework.stereotype.Service;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.model.Todo;
import com.kit.todo_litst_api.model.repository.TodoRepository;
import com.kit.todo_litst_api.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoResponse createTodo(TodoRequest request, Long userId) {
        var user = userRepository.getReferenceById(userId);
        var todo = Todo.builder()
                .title(request.title())
                .description(request.description())
                .user(user)
                .build();

        var savedTodo = todoRepository.save(todo);

        return TodoResponse.mapEntityToDto(savedTodo);
    }
}
