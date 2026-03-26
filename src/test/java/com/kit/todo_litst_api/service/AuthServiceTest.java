package com.kit.todo_litst_api.service;

import com.kit.todo_litst_api.dto.RegisterRequest;
import com.kit.todo_litst_api.exception.UsernameAlreadyExistsException;
import com.kit.todo_litst_api.model.User;
import com.kit.todo_litst_api.model.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldSaveUser_WhenUsernameDoesNotExist() {
        // Given
        RegisterRequest request = new RegisterRequest("testuser", "test@example.com", "password123");
        when(userRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn("encodedPassword");

        // When
        authService.registerUser(request);

        // Then
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();

        assertThat(request.username()).isEqualTo(savedUser.getUsername());
        assertThat(request.email()).isEqualTo(savedUser.getEmail());
        assertThat("encodedPassword").isEqualTo(savedUser.getPassword());
    }

    @Test
    void shouldThrowException_WhenUsernameExists() {
        // Given
        RegisterRequest request = new RegisterRequest("existinguser", "test@example.com", "password123");
        when(userRepository.existsByUsername(request.username())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessage("Username already exists");
        verify(userRepository, never()).save(any());
    }
}
