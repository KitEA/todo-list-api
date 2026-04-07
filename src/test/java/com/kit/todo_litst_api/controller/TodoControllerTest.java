package com.kit.todo_litst_api.controller;

import com.kit.todo_litst_api.config.JwtToUserConverter;
import com.kit.todo_litst_api.config.SecurityConfig;
import com.kit.todo_litst_api.dto.TodoRequest;
import com.kit.todo_litst_api.dto.TodoResponse;
import com.kit.todo_litst_api.model.User;
import com.kit.todo_litst_api.model.repository.UserRepository;
import com.kit.todo_litst_api.service.JwtService;
import com.kit.todo_litst_api.service.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@Import({SecurityConfig.class, JwtToUserConverter.class, JwtService.class})
class TodoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @MockitoBean
    private TodoService todoService;

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

        when(userRepository.getReferenceById(1L)).thenReturn(user);
        when(todoService.createTodo(any(), any())).thenReturn(response);

        var token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.description").value("Buy milk, eggs, and bread"));
    }

    @Test
    void shouldReturn401_WhenNoTokenProvided() throws Exception {
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn401_WhenTokenIsInvalid() throws Exception {
        var request = new TodoRequest("Buy groceries", "Buy milk, eggs, and bread");

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer this.is.not.a.valid.token")
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn400_WhenTitleIsBlank() throws Exception {
        var user = testUser();
        var request = new TodoRequest("", "Some description");

        when(userRepository.getReferenceById(1L)).thenReturn(user);

        var token = jwtService.generateToken(user);

        mockMvc.perform(post("/api/todos")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
