package com.example.habittracker.service;

import com.example.habittracker.dto.request.LoginRequest;
import com.example.habittracker.dto.request.RegisterRequest;
import com.example.habittracker.dto.response.AuthResponse;
import com.example.habittracker.entity.User;
import com.example.habittracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("jake");
        request.setEmail("jake@example.com");
        request.setPassword("123456");

        User savedUser = buildUser(
        1L,
        "jake",
        "jake@example.com",
        "encoded-password"
        );

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(jwtService.generateToken(savedUser)).thenReturn("jwt-token");

        AuthResponse response = authService.register(request);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("jake");
        assertThat(response.getEmail()).isEqualTo("jake@example.com");
        assertThat(response.getToken()).isEqualTo("jwt-token");

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User userToSave = userCaptor.getValue();

        assertThat(userToSave.getUsername()).isEqualTo("jake");
        assertThat(userToSave.getEmail()).isEqualTo("jake@example.com");
        assertThat(userToSave.getPassword()).isEqualTo("encoded-password");

        verify(passwordEncoder).encode("123456");
        verify(jwtService).generateToken(savedUser);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("jake");
        request.setEmail("jake@example.com");
        request.setPassword("123456");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(
        RuntimeException.class,
        () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("jake");
        request.setEmail("jake@example.com");
        request.setPassword("123456");

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThrows(
        RuntimeException.class,
        () -> authService.register(request)
        );

        verify(userRepository, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(any());
    }

    @Test
    void shouldLoginUser() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jake@example.com");
        request.setPassword("123456");

        User user = buildUser(
        1L,
        "jake",
        "jake@example.com",
        "encoded-password"
        );

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        AuthResponse response = authService.login(request);

        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("jake");
        assertThat(response.getEmail()).isEqualTo("jake@example.com");
        assertThat(response.getToken()).isEqualTo("jwt-token");

        verify(passwordEncoder).matches("123456", "encoded-password");
        verify(jwtService).generateToken(user);
    }

    @Test
    void shouldThrowExceptionWhenLoginEmailNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("unknown@example.com");
        request.setPassword("123456");

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(
        RuntimeException.class,
        () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).generateToken(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenLoginPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("jake@example.com");
        request.setPassword("wrong-password");

        User user = buildUser(
        1L,
        "jake",
        "jake@example.com",
        "encoded-password"
        );

        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.getPassword(), user.getPassword())).thenReturn(false);

        assertThrows(
        RuntimeException.class,
        () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any(User.class));
    }

    private User buildUser(Long id, String username, String email, String password) {
        User user = new User();

        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setCreatedAt(LocalDateTime.now());

        return user;
    }
}