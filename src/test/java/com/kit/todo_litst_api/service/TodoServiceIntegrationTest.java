package com.kit.todo_litst_api.service;

import com.kit.todo_litst_api.config.TestContainerConfig;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.model.repository.TodoRepository;
import com.kit.todo_litst_api.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestContainerConfig.class)
class TodoServiceIntegrationTest {

    @Autowired
    private TodoService todoService;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Test
    void shouldCreateTodoInDatabase() {
        // Given
        authService.registerUser(new RegisterRequest("todoUser1", "todo1@example.com", "password123"));
        var user = userRepository.findByEmail("todo1@example.com").orElseThrow();
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");

        // When
        var response = todoService.createTodo(request, user.getId());

        // Then
        assertThat(response.id()).isNotNull();
        assertThat(response.title()).isEqualTo("Buy groceries");
        assertThat(response.description()).isEqualTo("Buy milk, eggs, and bread");

        var savedTodo = todoRepository.findById(response.id());
        assertThat(savedTodo).hasValueSatisfying(it -> {
            assertThat(it.getTitle()).isEqualTo("Buy groceries");
            assertThat(it.getDescription()).isEqualTo("Buy milk, eggs, and bread");
            assertThat(it.getUser().getId()).isEqualTo(user.getId());
        });
    }

    @Test
    void shouldLinkTodoToCorrectUser() {
        // Given
        authService.registerUser(new RegisterRequest("userA", "userA@example.com", "password123"));
        authService.registerUser(new RegisterRequest("userB", "userB@example.com", "password123"));
        var userA = userRepository.findByEmail("userA@example.com").orElseThrow();
        var userB = userRepository.findByEmail("userB@example.com").orElseThrow();

        // When
        var responseA = todoService.createTodo(new TodoRequest("User A's task", null), userA.getId());
        var responseB = todoService.createTodo(new TodoRequest("User B's task", null), userB.getId());

        // Then
        var todoA = todoRepository.findById(responseA.id()).orElseThrow();
        var todoB = todoRepository.findById(responseB.id()).orElseThrow();

        assertThat(todoA.getUser().getId()).isEqualTo(userA.getId());
        assertThat(todoB.getUser().getId()).isEqualTo(userB.getId());
        assertThat(todoA.getUser().getId()).isNotEqualTo(todoB.getUser().getId());
    }

    @Test
    void shouldCreateTodo_WhenDescriptionIsNull() {
        // Given
        authService.registerUser(new RegisterRequest("todoUser2", "todo2@example.com", "password123"));
        var user = userRepository.findByEmail("todo2@example.com").orElseThrow();
        var request = new TodoRequest("No description task", null);

        // When
        var response = todoService.createTodo(request, user.getId());

        // Then
        assertThat(response.title()).isEqualTo("No description task");
        assertThat(response.description()).isNull();
    }
}
