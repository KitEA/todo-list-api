package com.kit.todo_litst_api.service;

import com.kit.todo_litst_api.config.TestContainerConfig;
import com.kit.todo_litst_api.dto.AuthResponse;
import com.kit.todo_litst_api.dto.LoginRequest;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.exception.UsernameAlreadyExistsException;
import com.kit.todo_litst_api.model.User;
import com.kit.todo_litst_api.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Import(TestContainerConfig.class)
@ActiveProfiles("test")
class AuthServiceIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void shouldCreateUserInDatabase() {
        // Given
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");

        // When
        authService.registerUser(request);

        // Then
        Optional<User> savedUserOptional = userRepository.findByUsername("testuser");
        assertThat(savedUserOptional).isPresent();
        
        User savedUser = savedUserOptional.get();
        assertThat(savedUser.getUsername()).isEqualTo("testuser");
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");

        assertThat(savedUser.getPassword()).isNotEqualTo("password123");
        assertThat(savedUser.getPassword()).startsWith("$2");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
    }

    @Test
    void shouldFail_WhenUsernameAlreadyExists() {
        // Given
        RegisterRequest request1 = new RegisterRequest("duplicateUser", "first@example.com", "password123");
        authService.registerUser(request1);

        RegisterRequest request2 = new RegisterRequest("duplicateUser", "second@example.com", "password456");

        // When & Then
        assertThatThrownBy(() -> authService.registerUser(request2))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("Username already exists");

        assertThat(userRepository.findAll())
                .extracting(User::getUsername)
                .containsOnlyOnce("duplicateUser");
    }

    @Test
    void shouldLoginSuccessfully_WhenValidCredentialsProvided() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("loginUser", "login@example.com", "password123");
        authService.registerUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("login@example.com", "password123");

        // When
        AuthResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.token()).isNotBlank();
    }

    @Test
    void shouldFailLogin_WhenInvalidPasswordProvided() {
        // Given
        RegisterRequest registerRequest = new RegisterRequest("wrongPassUser", "wrongpass@example.com", "password123");
        authService.registerUser(registerRequest);

        LoginRequest loginRequest = new LoginRequest("wrongpass@example.com", "wrongpassword");

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }

    @Test
    void shouldFailLogin_WhenUserDoesNotExist() {
        // Given
        LoginRequest loginRequest = new LoginRequest("notfound@example.com", "password123");

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid email or password");
    }
}
