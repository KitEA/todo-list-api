package com.kit.todo_litst_api.service;

import org.springframework.stereotype.Service;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.model.Todo;
import com.kit.todo_litst_api.model.repository.TodoRepository;
import com.kit.todo_litst_api.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
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

    public TodoResponse updateTodo(Long id, TodoRequest request, Long userId) {
        var todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        if (!todo.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to update this todo");
        }

        todo.setTitle(request.title());
        todo.setDescription(request.description());

        var updatedTodo = todoRepository.save(todo);
        return TodoResponse.mapEntityToDto(updatedTodo);
    }

    public void deleteTodo(Long id, Long userId) {
        var todo = todoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Todo not found"));

        if (!todo.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to delete this todo");
        }

        todoRepository.delete(todo);
    }
}
