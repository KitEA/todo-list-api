package com.kit.todo_litst_api.controller;

import com.kit.todo_litst_api.config.CustomAuthenticationEntryPoint;
import com.kit.todo_litst_api.config.JwtAuthenticationFilter;
import com.kit.todo_litst_api.config.SecurityConfig;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.model.User;
import com.kit.todo_litst_api.service.JwtService;
import com.kit.todo_litst_api.service.TodoService;
import com.kit.todo_litst_api.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, CustomAuthenticationEntryPoint.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TodoService todoService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserRepository userRepository;

    private User testUser() {
        return User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encoded-password")
                .build();
    }

    @Test
    void shouldCreateTodo_WhenValidTokenProvided() throws Exception {
        var user = testUser();
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");
        var response = new TodoResponse(1L, "Buy groceries", "Buy milk, eggs, and bread");

        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(todoService.createTodo(any(), any())).thenReturn(response);

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid-token")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.description").value("Buy milk, eggs, and bread"));
    }

    @Test
    void shouldReturn401_WhenNoTokenProvided() throws Exception {
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void shouldReturn401_WhenTokenIsInvalid() throws Exception {
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");

        when(jwtService.isTokenValid("invalid-token")).thenReturn(false);

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer invalid-token")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Unauthorized"));
    }

    @Test
    void shouldReturn400_WhenTitleIsBlank() throws Exception {
        var user = testUser();
        var request = new TodoRequest("", "Some description");

        when(jwtService.isTokenValid("valid-token")).thenReturn(true);
        when(jwtService.extractEmail("valid-token")).thenReturn("test@example.com");
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer valid-token")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
