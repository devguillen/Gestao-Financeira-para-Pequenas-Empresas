package com.gestao.financas.test;

import com.gestao.financas.entity.User;
import com.gestao.financas.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SecurityLogService securityLogService;

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private AuthService authService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encodedPassword");
        user.setFailedLoginAttempts(0);
    }

    @Test
    void testLoginSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        String token = authService.login("testuser", "correctPassword");

        assertNotNull(token);
        verify(userRepository, times(1)).save(user);
        verify(securityLogService, times(1)).logEvent(user.getId(), "LOGIN_SUCCESS", "127.0.0.1");
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.login("unknown", "anyPassword")
        );

        assertEquals("Usuário não encontrado", ex.getMessage());
    }

    @Test
    void testLoginIncorrectPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.login("testuser", "wrongPassword")
        );

        assertEquals("Senha incorreta", ex.getMessage());
        verify(userRepository, times(1)).save(user);
        verify(securityLogService, times(1)).logEvent(user.getId(), "LOGIN_FAILURE", "127.0.0.1");
    }

    @Test
    void testLoginAccountLocked() {
        user.setLockTime(System.currentTimeMillis() + 60000); // bloqueada por 1 minuto
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                authService.login("testuser", "anyPassword")
        );

        assertEquals("Conta bloqueada. Tente novamente mais tarde.", ex.getMessage());
        verify(securityLogService, times(1)).logEvent(user.getId(), "LOGIN_FAILURE_BLOCKED", "127.0.0.1");
        verify(userRepository, never()).save(user);
    }
}
