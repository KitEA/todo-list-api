package com.kit.todo_litst_api.service;

import com.kit.todo_litst_api.config.TestContainerConfig;
import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.model.User;
import com.kit.todo_litst_api.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThrows(RuntimeException.class, () -> authService.registerUser(request2));

        assertThat(userRepository.findAll())
                .extracting(User::getUsername)
                .containsOnlyOnce("duplicateUser");
    }
}
