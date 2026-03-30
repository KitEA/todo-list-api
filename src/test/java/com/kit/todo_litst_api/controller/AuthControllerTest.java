package com.kit.todo_litst_api.controller;

import com.kit.todo_litst_api.config.JwtToUserConverter;
import com.kit.todo_litst_api.config.SecurityConfig;
import com.kit.todo_litst_api.dto.AuthResponse;
import com.kit.todo_litst_api.dto.LoginRequest;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.model.repository.UserRepository;
import com.kit.todo_litst_api.service.AuthService;
import com.kit.todo_litst_api.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, JwtToUserConverter.class, JwtService.class})
@TestPropertySource(properties = "jwt.secret=this-is-a-very-long-secret-key-for-testing-purposes")
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    AuthService authService;

    @MockitoBean
    UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        var request = new RegisterRequest("testuser", "test@example.com", "password123");
        var response = new AuthResponse("dummy-token");

        when(authService.registerUser(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    @Test
    void shouldReturn400_WhenEmailIsInvalid() throws Exception {
        var request = new RegisterRequest("user", "not-an-email", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenPasswordIsTooSimple() throws Exception {
        var request = new RegisterRequest("user", "test@example.com", "123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenUsernameIsBlank() throws Exception {
        var request = new RegisterRequest("", "test@example.com", "password123");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginSuccessfully() throws Exception {
        var request = new LoginRequest("test@example.com", "password123");
        var response = new AuthResponse("dummy-token");

        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-token"));
    }

    @Test
    void shouldReturn400_WhenLoginEmailIsInvalid() throws Exception {
        var request = new LoginRequest("not-an-email", "password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400_WhenLoginPasswordIsBlank() throws Exception {
        var request = new LoginRequest("test@example.com", "");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}